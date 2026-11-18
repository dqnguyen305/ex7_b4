package com.example.banking;

import java.math.BigDecimal;

public record Account(
        Long id,
        String accountNumber,
        String accountHolderName,
        BigDecimal balance
) {
}
