package com.docomodigital.delorean.voucher.web.controller.error;

/**
 * 2020/01/28
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class ErrorDetails {
    private String errorCode;
    private String errorMessage;

    public ErrorDetails(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
