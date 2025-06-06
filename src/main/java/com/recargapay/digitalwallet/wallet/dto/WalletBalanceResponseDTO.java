package com.recargapay.digitalwallet.wallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletBalanceResponseDTO {

  private Long accountNumber;
  private BigDecimal balance;
}
