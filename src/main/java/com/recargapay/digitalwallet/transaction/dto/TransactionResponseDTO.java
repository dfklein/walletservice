package com.recargapay.digitalwallet.transaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {

  private String id;
  private TransactionDTOType type;
  private BigDecimal amount;
  private Long fromWalletNumber;
  private Long toWalletNumber;
  private LocalDateTime timestamp;

}
