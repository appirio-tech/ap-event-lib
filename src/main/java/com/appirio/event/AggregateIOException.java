package com.appirio.event;

import java.io.IOException;
import java.util.List;

/**
 * Created by thabo on 3/27/15.
 */
public class AggregateIOException extends Exception {
    private final List<IOException> internalExceptions;

    public AggregateIOException(List<IOException> ioExceptions) {
        super();
        internalExceptions = ioExceptions;
    }

    public List<IOException> getInternalExceptions() {
        return internalExceptions;
    }
}
