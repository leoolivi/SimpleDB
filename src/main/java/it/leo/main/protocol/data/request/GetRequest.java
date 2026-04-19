package it.leo.main.protocol.data.request;

import java.io.Serializable;

public record GetRequest(
    String key
) implements Serializable {
    
}
