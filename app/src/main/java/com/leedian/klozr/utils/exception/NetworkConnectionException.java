package com.leedian.klozr.utils.exception;
/**
 * NetworkConnectionException
 *
 * @author Franco
 */
public class NetworkConnectionException
        extends DomainException
{
    static int SYS_NO_NETWORK = 06;
    private int errno_code;

    public NetworkConnectionException() {

        super();
    }

    public NetworkConnectionException(final String message, int domainError, final Throwable cause) {

        super(message, cause);
        errno_code = domainError;
    }

    public NetworkConnectionException(final Throwable cause) {

        super(cause);
    }

    public int getErrno_code() {

        return errno_code;
    }
}
