package com.store.E_Commerce.exception;

public class UserNotVerifiedException extends Exception {
    private boolean newEmailSend;

    public UserNotVerifiedException(boolean newEmailSend) {
        this.newEmailSend = newEmailSend;
    }

    public boolean isNewEmailSent() {
        return newEmailSend;
    }
}
