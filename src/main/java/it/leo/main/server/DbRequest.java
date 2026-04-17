package it.leo.main.server;

import it.leo.main.server.enums.RequestType;

public record DbRequest (
    RequestType type, 
    String payload) {}