package com.recargapay.digitalwallet.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<AppExceptionDTO> handleBusinessException(BusinessException ex) {
    return ResponseEntity
        .status(ex.getHttpStatus())
        .body(AppExceptionDTO.builder()
            .message(ex.getMessage())
            .build());
  }
}
