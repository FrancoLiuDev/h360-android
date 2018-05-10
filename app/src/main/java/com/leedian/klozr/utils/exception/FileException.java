package com.leedian.klozr.utils.exception;
/**
 * FileException
 *
 * @author Franco
 */
public class FileException
        extends DomainException
{
    public FileException() {

        super();
    }

    public FileException(final String message) {

        super(message);
    }

    public FileException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public FileException(final Throwable cause) {

        super(cause);
    }
}
