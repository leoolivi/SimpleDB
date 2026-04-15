package it.leo.main.utility;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArgParser {

    public static Map<String, Object> parseArgs(String input) {
        // Definiamo il pattern (stessa regex di prima)
        Pattern pattern = Pattern.compile("\"([^\"]*)\":\"([^\"]*)\"");

        // Approccio funzionale con Java 9+
        return pattern.matcher(input)
                .results() // Crea uno Stream<MatchResult>
                .collect(Collectors.toMap(
                        m -> m.group(1), // Key: primo gruppo
                        m -> m.group(2), // Value: secondo gruppo
                        (ex, _new) -> ex // Merge function (in caso di chiavi doppie)
                ));
    }
}
