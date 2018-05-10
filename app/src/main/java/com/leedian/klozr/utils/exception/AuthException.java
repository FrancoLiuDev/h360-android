package com.leedian.klozr.utils.exception;
/**
 * AuthException
 *
 * @author Franco
 */
public class AuthException
        extends DomainException
{
    public AuthException() {

        super();
    }

    public AuthException(int major, int minor, final String message) {

        super(major, minor, message);
    }

    public AuthException(final String message) {

        super(message);
    }

    public AuthException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public AuthException(final Throwable cause) {

        super(cause);
    }

    public boolean isAuthFailPassword() {

        return (this.exceptionMajor == 1 && this.exceptionMinor == 5);
    }

    public boolean isAuthFailName() {

        return (this.exceptionMajor == 2 && this.exceptionMinor == 2);
    }
}
