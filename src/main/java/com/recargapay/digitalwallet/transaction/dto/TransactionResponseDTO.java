package com.recargapay.digitalwallet.transaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {

  private UUID transactionTracerId;
  private TransactionDTOType type;
  private BigDecimal amount;
  private Long fromWalletNumber;
  private Long toWalletNumber;
  private LocalDateTime timestamp;

}
