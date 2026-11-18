package com.example.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull @Positive Long sourceAccountId,
        @NotNull @Positive Long destinationAccountId,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {
}
