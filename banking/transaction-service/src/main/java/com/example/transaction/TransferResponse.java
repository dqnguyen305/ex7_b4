package com.example.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        long sourceAccountId,
        long destinationAccountId,
        BigDecimal amount,
        String status
) {
}
