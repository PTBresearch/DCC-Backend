package de.ptb.dsi.dcc_backend.exception;

public class DccNotFoundException extends RuntimeException {
    public DccNotFoundException(String pid) {
        super("DCC not found for PID: " + pid);
    }
}