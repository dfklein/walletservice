package com.recargapay.digitalwallet.person.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonResponseDTO {

  private String fullName;
  private String documentNumber;

}
