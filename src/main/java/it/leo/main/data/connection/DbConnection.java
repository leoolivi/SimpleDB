package it.leo.main.data.connection;

/*

Fields to store in a DB Conn
- Session ID to bind the client to a specific thread
- Server version
- Charset es. UTF8, Latin1
- Limits (optional): max rows returnable, timeouts

*/
public final class DbConnection {
    private final String UUID;
    private final String serverVersion;
    private final String charset;

    public DbConnection(String UUID, String charset, String serverVersion) {
        this.UUID = UUID;
        this.charset = charset;
        this.serverVersion = serverVersion;
    }

    public String getUUID() {
        return UUID;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getCharset() {
        return charset;
    }
}
