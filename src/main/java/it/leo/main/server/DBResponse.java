package it.leo.main.server;

import java.util.List;
import java.util.stream.Collectors;

import it.leo.main.server.enums.ResponseStatus;
import it.leo.main.server.persistence.interfaces.DBRow;

public record DBResponse<K, V>(
    String msg,
    ResponseStatus status,
    List<DBRow<K, V>> rows
) {
    
    public DBResponse { rows = rows != null ? rows : List.of(); }
    
    public List<String> toLines() {
        String str = "[%s, %s]";
        var lines = rows.stream().map(row -> String.format(str, row.getKey(), row.getValue())).collect(Collectors.toList());
        lines.add("EOF");
        return lines;
    }
}
