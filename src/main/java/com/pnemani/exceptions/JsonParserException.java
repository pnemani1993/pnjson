package com.pnemani.exceptions;

public final class JsonParserException extends Exception {
    private final int POSITION;

    public JsonParserException(String message, int position) {
        super(message);
        this.POSITION = position;
    }

    public int getPosition() {
        return this.POSITION;
    }
    
}
