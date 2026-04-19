# Code Review & Debug Report
### Java Simple Key-Value DB

> **Revisore:** Claude Sonnet  
> **Data:** Aprile 2026  
> **Scope:** Bug critico (client bloccato) + revisione architetturale completa  
> **Livello progetto:** Learning / Low-level Java

---

## Indice

1. [Bug Critico — Client bloccato](#1-bug-critico--client-bloccato)
2. [Bug Secondari](#2-bug-secondari)
3. [Code Smells & Design Issues](#3-code-smells--design-issues)
4. [Punti Forti](#4-punti-forti)
5. [Soluzioni — Codice Corretto](#5-soluzioni--codice-corretto)
6. [Panoramica Generale](#6-panoramica-generale)
7. [Roadmap Suggerita](#7-roadmap-suggerita)

---

## 1. Bug Critico — Client bloccato

**Severità:** 🔴 CRITICAL  
**File coinvolti:** `ConnectionThread.java`, `CLIService.java`, `DbConnection.java`

### Root Cause

Il client rimane appeso per un deadlock nella gestione degli stream del socket. La causa è duplice.

#### Problema A — Circular block tra `ObjectOutputStream` e `ObjectInputStream`

Il server, nel costruttore di `DbConnection`, crea già `BufferedReader` e `PrintWriter` sull'input/output stream del socket:

```java
// DbConnection.java — costruttore
this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
this.printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
```

Poi `ConnectionThread.run()` apre un **terzo stream** sullo stesso socket per serializzare l'oggetto:

```java
// ConnectionThread.java
ObjectOutputStream outputStream = new ObjectOutputStream(connection.getClientSocket().getOutputStream());
outputStream.writeObject(connection);
```

`ObjectOutputStream` nel costruttore **scrive immediatamente un header di 4 byte** sul flusso prima ancora di serializzare l'oggetto. Il client, dall'altra parte:

```java
// CLIService.java
ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
```

`ObjectInputStream` nel **costruttore blocca** aspettando di leggere quell'header. Se i buffer interni non vengono flushati nel momento giusto, i due lati si aspettano a vicenda indefinitamente — **circular block**.

#### Problema B — Stream multipli sullo stesso socket (corruzione dati)

Anche se il deadlock si risolvesse da solo, esiste un problema strutturale più grave: tre stream concorrenti sullo stesso `OutputStream` del socket.

```
Socket.getOutputStream()
    ├── PrintWriter (creato in DbConnection)       ← usato per le query
    └── ObjectOutputStream (creato in ConnectionThread) ← usato per l'handshake
```

I byte della serializzazione Java (`¬ísr...`) vengono scritti sullo stesso canale del `PrintWriter`. Quando il client legge le risposte delle query col `BufferedReader`, trova nel mezzo byte binari di serializzazione → **protocollo corrotto**, risposta illeggibile o eccezione silenziosa.

#### Flusso temporale del deadlock

```
SERVER                                  CLIENT
  |                                       |
  | new ObjectOutputStream(out) ───────►  |
  |   (scrive header 4 byte)              |
  |                                       | new ObjectInputStream(in)
  |   ◄─── aspetta ACK dell'header ───── |   (aspetta header dal server)
  |                                       |
  |   [DEADLOCK: nessuno procede]         |
```

### Fix — Eliminare la serializzazione, usare handshake testuale

La serializzazione di `DbConnection` è concettualmente sbagliata per questo scopo: il client non ha bisogno dell'oggetto Java, ha bisogno dei **metadati di connessione come testo**. Un semplice handshake testuale risolve tutto e mantiene un solo stream per direzione.

Vedi il codice corretto nella [Sezione 5](#5-soluzioni--codice-corretto).

---

## 2. Bug Secondari

### 2.1 — `InvalidQueryException` non catturata

**Severità:** 🔴 HIGH  
**File:** `PacketProcessor.java` — metodo `processNextPacket()`

```java
// Il throw è presente...
if (query.matches(".*[,./!@#$%^&*()_+{}\\[\\]\\\\|;:'\"/?<>].*"))
    throw new InvalidQueryException("Invalid character found");

// ...ma nel catch block manca la gestione:
} catch (IOException e) {
    e.printStackTrace();
} catch (InterruptedException | ExecutionException ex) {
    ...
}
// ← InvalidQueryException è una RuntimeException, propaga non catturata
```

**Conseguenza:** Se il client invia una query con caratteri speciali (es. `GET key!`), l'eccezione propaga fuori da `processNextPacket()`, sale in `ConnectionThread.run()` che non la cattura, il thread muore silenziosamente e la connessione rimane appesa senza risposta.

**Fix:**
```java
} catch (InvalidQueryException e) {
    return new DBResponse<>("Invalid query: " + e.getMessage(), ResponseStatus.ERROR, Collections.emptyList());
}
```

---

### 2.2 — `subList` prima della validazione della lunghezza

**Severità:** 🔴 HIGH  
**File:** `PacketProcessor.java`

```java
// Prima tronca...
queryChunks = queryChunks.subList(0, QueryCommand.get().expectedTokens());

// ...poi valida
if (QueryCommand.get().expectedTokens() != queryChunks.size()) {
    // Questa condizione non sarà mai true dopo il subList!
}
```

Questo codice ha due problemi distinti:

**A)** Se l'utente scrive `GET` senza argomento, `queryChunks` ha 1 elemento ma `expectedTokens()` ritorna 2. La chiamata `subList(0, 2)` su una lista di 1 elemento lancia `IndexOutOfBoundsException` non catturata.

**B)** Anche se la lista fosse abbastanza lunga, dopo il `subList` la condizione `expectedTokens() != queryChunks.size()` sarà sempre falsa per costruzione — la validazione è inutile.

**Fix:** Validare la lunghezza **prima** di troncare.

```java
if (queryChunks.size() < command.expectedTokens()) {
    return new DBResponse<>(
        "Wrong number of arguments for " + commandStr + ": expected "
        + command.expectedTokens() + " got " + queryChunks.size(),
        ResponseStatus.ERROR, Collections.emptyList()
    );
}
queryChunks = queryChunks.subList(0, command.expectedTokens());
```

---

### 2.3 — `null` da `readLine()` non gestito

**Severità:** 🟠 MEDIUM  
**File:** `PacketProcessor.java`

```java
String query = bufferedReader.readLine();
// query può essere null se il client chiude la connessione
if (query.matches(...)) // ← NullPointerException
```

Quando il client chiude il socket, `readLine()` ritorna `null`. La chiamata a `query.matches(...)` lancia `NullPointerException`, il thread del server crasha.

**Fix:**
```java
String query = bufferedReader.readLine();
if (query == null) {
    return new DBResponse<>("Client disconnected", ResponseStatus.ERROR, Collections.emptyList());
}
```

---

### 2.4 — `CsvRepository` non è thread-safe

**Severità:** 🟠 MEDIUM  
**File:** `CsvRepository.java`

L'operazione `set()` legge il file, scrive su un file temporaneo, poi fa `Files.move()`. Se due client eseguono `SET` contemporaneamente:

```
Thread A: legge db.csv ───►
Thread B: legge db.csv ───►
Thread A: scrive tmp_A.csv, move → db.csv
Thread B: scrive tmp_B.csv, move → db.csv  ← sovrascrive il lavoro di A
```

Il secondo `move` sovrascrive il risultato del primo. Un record scritto da Thread A viene perso.

**Fix:** Proteggere la sezione critica con un `ReadWriteLock` (più reads parallele, write esclusiva):

```java
private final ReadWriteLock lock = new ReentrantReadWriteLock();

@Override
public List<DBRow<String, String>> findAll() throws IOException {
    lock.readLock().lock();
    try {
        // ... logica invariata
    } finally {
        lock.readLock().unlock();
    }
}

@Override
public DBRow<String, String> set(String key, String value) throws IOException {
    lock.writeLock().lock();
    try {
        // ... logica invariata
    } finally {
        lock.writeLock().unlock();
    }
}
```

Nota: questo è esattamente uno dei learning goal dichiarati nel README — ottimo punto da implementare.

---

### 2.5 — `CLIService`: flusso di connessione non ritorna in caso di errore

**Severità:** 🟡 LOW  
**File:** `CLIService.java`

```java
if (argsMap.size() != 2) {
    System.err.println("Invalid command syntax: Expected 2 arguments got " + argsMap.size());
    // manca return — il codice continua comunque e prova a connettersi
} else {
    // questo else è superfluo se aggiungi return sopra
}
```

**Fix:** Aggiungere `return` dopo il messaggio di errore.

---

## 3. Code Smells & Design Issues

### 3.1 — Raw type su `DBRepository` in `PacketProcessor`

**Severità:** 🟡 LOW  
**File:** `PacketProcessor.java`

```java
private final DBRepository dbRepository; // ← raw type, nessun generic

// Poi costretto a castare ovunque:
var task = new GetRecordTask(key, (DBRepository<String, String>) dbRepository);
```

**Fix:**
```java
private final DBRepository<String, String> dbRepository;
```

Il cast sparisce, il compilatore può fare type-checking corretto.

---

### 3.2 — `Command` ha setter ma le istanze sono costanti statiche

**Severità:** 🟡 LOW  
**File:** `Command.java`, `CLICommandFactory.java`, `QueryCommandFactory.java`

Le costanti in `CLICommandFactory` e `QueryCommandFactory` sono `public static final`, ma `Command` espone setter pubblici. Chiunque può mutarle:

```java
CLICommandFactory.HELP.setName("something_else"); // compilabile e pericoloso
```

**Fix:** Rimuovere i setter da `Command` e rendere i campi `final`. Se serve mutabilità in futuro, creare una classe separata `MutableCommand`.

---

### 3.3 — Validazione della query accoppiata al `PacketProcessor`

**Severità:** 🟡 LOW  
**File:** `PacketProcessor.java`

Il parsing e la validazione della query vivono dentro `processNextPacket()`. Man mano che aggiungi comandi questa funzione crescerà enormemente e diventerà difficile da testare.

**Suggerimento:** Estrarre un `QueryParser` dedicato:

```java
public class QueryParser {
    public ParsedQuery parse(String raw) throws InvalidQueryException { ... }
}

public record ParsedQuery(QueryCommand command, List<String> args) {}
```

`PacketProcessor` diventa responsabile solo del ciclo request/response, non del parsing.

---

### 3.4 — `DbRequest` è definito ma mai usato

**File:** `DbRequest.java`

Il record `DbRequest` con `RequestType` esiste ma non viene mai istanziato né usato nel codice. È tech debt o una struttura pianificata per il futuro?

Se è pianificata, aggiungere un commento `// TODO: da integrare nel protocollo`. Se è un residuo, rimuoverla per non confondere chi legge.

---

### 3.5 — `App.java` è un file placeholder inutile

**File:** `App.java`

```java
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
```

Non fa nulla di utile. Rimuoverlo o trasformarlo in documentazione dell'architettura (es. entry point per lanciare server+client in modalità embedded per i test).

---

### 3.6 — `AppTest.java` è un test trivialmente vero

**File:** `AppTest.java`

```java
public void testApp() {
    assertTrue(true); // sempre vero, non testa nulla
}
```

È un placeholder generato da Maven. Non dà nessuna copertura. Vedi [Sezione 7](#7-roadmap-suggerita) per suggerimenti su cosa testare prima.

---

### 3.7 — Logging con `System.getLogger` misto a `System.out.println`

Alcune classi usano `System.getLogger(...).log(...)`, altre usano `System.out.println("DEBUG: ...")` sparsi nel codice. I messaggi di debug dovrebbero passare per il logger, non per stdout, in modo da poterli silenziare in produzione. Idealmente si introduce una dipendenza come SLF4J + Logback.

---

## 4. Punti Forti

Nonostante i bug, il progetto mostra una struttura notevolmente matura per un progetto di apprendimento.

| Aspetto | Valutazione |
|---|---|
| Separazione in package (client / server / protocol / persistence / config / tasks) | ✅ Ottima |
| Interfaccia `DBRepository<K,V>` + implementazione `CsvRepository` | ✅ DI-friendly, facilmente mockabile |
| Pattern Builder su `QueryCommand` | ✅ Appropriato per oggetti con parametri opzionali |
| `Callable` + `ThreadPoolExecutor` per le task I/O | ✅ Scelta corretta per separare I/O da logica |
| `DBTask<K,V>` come base astratta per i task | ✅ Buona astrazione |
| `DbConnection` con `AutoCloseable` | ✅ Resource management corretto |
| `DBResponse` come record immutabile | ✅ Uso moderno di Java |
| `expectedTokens()` su `QueryCommand` | ✅ Idea elegante per la validazione della struttura |
| README con indice, tabella, checklist | ✅ Ben strutturato per un primo README |

---

## 5. Soluzioni — Codice Corretto

### Fix principale: `ConnectionThread.java`

Sostituire la serializzazione con un handshake testuale.

```java
@Override
public void run() {
    // Handshake testuale: niente ObjectOutputStream, niente stream duplicati
    String handshake = String.format(
        "CONNECTED uuid=%s version=%s charset=%s",
        connection.getUUID(),
        connection.getServerVersion(),
        connection.getCharset()
    );
    connection.getPrintWriter().println(handshake);
    connection.getPrintWriter().println("EOF");
    connection.getPrintWriter().flush();

    while (!connection.isClosed()) {
        var response = processor.processNextPacket();
        switch (response.status()) {
            case DATA -> response.toLines().forEach(
                line -> connection.getPrintWriter().println(line)
            );
            default -> {
                connection.getPrintWriter().println(response.msg());
                connection.getPrintWriter().println("EOF");
            }
        }
        connection.getPrintWriter().flush();
    }
}
```

---

### Fix principale: `CLIService.java`

```java
private void handleDBConnection(String args) {
    System.out.println("Connecting to the database...");

    var argsMap = ArgParser.parseArgs(args);

    if (argsMap.size() != 2) {
        System.err.println("Invalid command syntax: Expected 2 arguments, got " + argsMap.size());
        return; // ← fix 2.5: return mancante
    }

    try (Socket client = new Socket(ApplicationConfig.SERVER_HOST, ApplicationConfig.SERVER_PORT)) {
        // Un solo BufferedReader e PrintWriter — niente ObjectInputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

        // Leggi handshake
        String line;
        System.out.println("[Server handshake]");
        while (!(line = reader.readLine()).equals("EOF")) {
            System.out.println("  " + line);
        }

        // Costruisci DbConnection con gli stream già aperti
        DbConnection connection = new DbConnection(
            "client-side", ApplicationConfig.CHARSET, client, ApplicationConfig.SERVER_VERSION
        );
        connection.setBufferedReader(reader);
        connection.setPrintWriter(writer);
        connection.setClientSocket(client);

        new QueryService(connection).start();

    } catch (IOException ex) {
        System.getLogger(CLIService.class.getName())
            .log(System.Logger.Level.ERROR, (String) null, ex);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
```

---

### Fix: `PacketProcessor.java` — tutti i fix secondari insieme

```java
public DBResponse<String, String> processNextPacket() {
    try {
        String query = bufferedReader.readLine();

        // Fix 2.3: null check per client disconnesso
        if (query == null) {
            return new DBResponse<>("Client disconnected",
                ResponseStatus.ERROR, Collections.emptyList());
        }

        // Fix 2.1: cattura InvalidQueryException invece di propagarla
        if (query.matches(".*[,./!@#$%^&*()_+{}\\[\\]\\\\|;:'\"/?<>].*")) {
            return new DBResponse<>("Invalid character in query",
                ResponseStatus.ERROR, Collections.emptyList());
        }

        List<String> queryChunks = Arrays.stream(query.split("\\s+")).toList();
        String commandStr = queryChunks.getFirst();

        // Ricerca comando (più idiomatica con stream)
        Optional<QueryCommand> foundCommand = QueryCommandFactory.ALL.stream()
            .filter(c -> c.getName().equals(commandStr))
            .findFirst();

        if (foundCommand.isEmpty()) {
            return new DBResponse<>("Invalid command: " + commandStr,
                ResponseStatus.ERROR, Collections.emptyList());
        }

        QueryCommand command = foundCommand.get();

        // Fix 2.2: valida PRIMA di fare subList
        if (queryChunks.size() < command.expectedTokens()) {
            return new DBResponse<>(
                "Wrong number of arguments for " + commandStr
                + ": expected " + command.expectedTokens()
                + " got " + queryChunks.size(),
                ResponseStatus.ERROR, Collections.emptyList()
            );
        }

        queryChunks = queryChunks.subList(0, command.expectedTokens());

        switch (commandStr) {
            case "GET" -> {
                var task = new GetRecordTask(queryChunks.get(1), dbRepository);
                return threadPoolExecutor.submit(task).get();
            }
            case "SET" -> {
                var task = new SetRecordTask(queryChunks.get(1), queryChunks.get(3), dbRepository);
                return threadPoolExecutor.submit(task).get();
            }
            case "GETALL" -> {
                var task = new GetAllRecordTask(dbRepository);
                return threadPoolExecutor.submit(task).get();
            }
            default -> {
                return new DBResponse<>("Unknown command: " + commandStr,
                    ResponseStatus.ERROR, Collections.emptyList());
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException | ExecutionException ex) {
        System.getLogger(PacketProcessor.class.getName())
            .log(System.Logger.Level.ERROR, (String) null, ex);
    }

    return new DBResponse<>("Unexpected server error",
        ResponseStatus.ERROR, Collections.emptyList());
}
```

---

## 6. Panoramica Generale

### Architettura attuale

```
MainClient
  └── CLIService          ← gestione comandi CLI
        └── QueryService  ← invio query via socket

MainServer
  └── ConnectionThread    ← 1 thread per client
        └── PacketProcessor   ← parsing + dispatch
              └── DB*Task     ← eseguiti su worker pool
                    └── CsvRepository  ← I/O su file
```

La separazione è corretta e segue un'intuizione DDD basilare: ogni layer ha una responsabilità chiara. Il problema principale è che il **layer di comunicazione** (handshake via serializzazione) è implementato in modo incoerente col resto del protocollo testuale.

### README

Ben strutturato per essere il primo. Due piccoli errori:

- La sezione "Stack" inizia con "Here's all the **features**" ma descrive lo stack — piccola imprecisione lessicale.
- "If you have tips or any suggestion, feel free to ask" è grammaticalmente corretto ma semanticamente strano in un README pubblico su GitHub. Un lettore casuale non sa a chi chiedere. Considera: *"Contributions and feedback are welcome — open an issue or a PR."*

---

## 7. Roadmap Suggerita

Basata sui learning goal dichiarati nel README, ordinata per priorità di apprendimento.

| Priorità | Task | Concetto appreso |
|---|---|---|
| 1 | Fix bug critico (handshake testuale) | Socket streams, protocolli testuali |
| 2 | Fix `PacketProcessor` (null, subList, exception) | Defensive programming |
| 3 | `ReadWriteLock` su `CsvRepository` | Concorrenza, race condition — core learning goal |
| 4 | Scrivere test per `CsvRepository` (JUnit 5) | Unit testing, mockare I/O |
| 5 | Scrivere test per `PacketProcessor` con mock repository | Mocking, separazione responsabilità |
| 6 | Estrarre `QueryParser` da `PacketProcessor` | SRP, testabilità |
| 7 | Rendere `Command` immutabile (rimuovere setter) | Immutabilità, design difensivo |
| 8 | Implementare `setSoTimeout` sul socket | Gestione timeout, connessioni zombie |
| 9 | Sostituire `System.out.println` con SLF4J | Logging best practice |
| 10 | Aggiungere autenticazione nell'handshake | Security layer (learning goal avanzato) |

---

*Fine del report — versione 1.0*