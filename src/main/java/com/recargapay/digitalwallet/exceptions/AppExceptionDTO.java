package com.recargapay.digitalwallet.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppExceptionDTO {

  private String message;

}
