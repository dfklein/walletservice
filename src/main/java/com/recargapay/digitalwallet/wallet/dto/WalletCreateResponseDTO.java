package com.recargapay.digitalwallet.wallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletCreateResponseDTO {

  private Long number;
  private String descriprion;
  private BigDecimal balance;

}
