package it.leo.main.data;

import it.leo.main.data.enums.RequestType;

public record DbRequest (
    RequestType type, 
    String payload) {}