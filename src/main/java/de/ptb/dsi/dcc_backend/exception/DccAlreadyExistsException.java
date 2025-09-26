package de.ptb.dsi.dcc_backend.exception;

public class DccAlreadyExistsException extends RuntimeException {
    public DccAlreadyExistsException(String pid) {
        super("A DCC with the PID '" + pid + "' already exists");
    }
}
