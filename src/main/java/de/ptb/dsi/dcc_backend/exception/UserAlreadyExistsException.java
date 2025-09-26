package de.ptb.dsi.dcc_backend.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String userName) {
        super(" Username " + userName + "' already exists");
    }
}