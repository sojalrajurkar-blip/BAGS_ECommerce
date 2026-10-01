package com.rora.backend.payment.entity;

public enum TransactionType {
    PAYMENT_ATTEMPT,
    CAPTURE,
    AUTHORIZE,
    REFUND,
    VOID
}
