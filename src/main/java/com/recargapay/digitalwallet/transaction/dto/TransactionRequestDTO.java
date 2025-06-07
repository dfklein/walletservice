package com.recargapay.digitalwallet.transaction.dto;

import java.math.BigDecimal;

public record TransactionRequestDTO(

    Long destinationAccountNumber,
    BigDecimal amount
) {
}
