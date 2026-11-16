package com.booknplay.payment.exception;

import lombok.Getter;
@Getter
public class BookingConfirmationException extends RuntimeException {
    private final int status;
    private final String responseBody;
    public BookingConfirmationException(int status, String responseBody) {
        super(responseBody);
        this.status = status;
        this.responseBody = responseBody;
    }
}