/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2019.
 */
package dev.galasa.framework.spi;

public class InsufficientResourcesAvailableException extends ResourcePoolingServiceException {
    private static final long serialVersionUID = 1L;

    public InsufficientResourcesAvailableException() {
    }

    public InsufficientResourcesAvailableException(String message) {
        super(message);
    }

    public InsufficientResourcesAvailableException(Throwable cause) {
        super(cause);
    }

    public InsufficientResourcesAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientResourcesAvailableException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
