package com.pnemani.exceptions;

public class InvalidOperationException extends IllegalStateException {

    private int position;

    public InvalidOperationException(String message, int pos) {
        super(message);
        this.position = pos;
    }

    public int getPosition() {
        return this.position;
    }
}
