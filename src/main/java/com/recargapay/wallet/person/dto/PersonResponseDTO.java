package com.recargapay.wallet.person.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonResponseDTO {

  private String fullName;
  private String documentNumber;

}
