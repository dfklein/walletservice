package com.recargapay.digitalwallet.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends Exception {

  private final HttpStatus httpStatus;

  public BusinessException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
