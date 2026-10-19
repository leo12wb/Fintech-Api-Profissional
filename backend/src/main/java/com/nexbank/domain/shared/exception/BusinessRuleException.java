package com.nexbank.domain.shared.exception;

public class BusinessRuleException extends DomainException {
    private final String errorCode;
    public BusinessRuleException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public String getErrorCode() { return errorCode; }
}
