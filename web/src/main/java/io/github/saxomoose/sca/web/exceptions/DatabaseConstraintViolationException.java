package io.github.saxomoose.sca.web.exceptions;

import io.github.saxomoose.sca.web.enums.DatabaseConstraint;

public class DatabaseConstraintViolationException extends Exception {
    private final DatabaseConstraint databaseConstraint;

    public DatabaseConstraintViolationException(DatabaseConstraint databaseConstraint) {
        this.databaseConstraint = databaseConstraint;
    }

    public DatabaseConstraint databaseConstraint() { return databaseConstraint; }
}
