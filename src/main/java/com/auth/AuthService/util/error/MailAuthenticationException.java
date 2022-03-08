package com.auth.AuthService.util.error;

public class MailAuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 3874658739837457523L;

    public MailAuthenticationException() {
        super();
    }

    public MailAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MailAuthenticationException(final String message) {
        super(message);
    }

    public MailAuthenticationException(final Throwable cause) {
        super(cause);
    }
}
