package com.recargapay.digitalwallet.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletResponseDTO {

  private Long accountNumber;
  private String descriprion;
  private BigDecimal balance;
  private BigDecimal overdraftLimit;

}
