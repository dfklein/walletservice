package com.recargapay.digitalwallet.transaction.controller;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.transaction.dto.TransactionRequestDTO;
import com.recargapay.digitalwallet.transaction.dto.TransactionResponseDTO;
import com.recargapay.digitalwallet.transaction.service.TransactionService;
import com.recargapay.digitalwallet.wallet.dto.WalletResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/wallets/{accountNumber}/withdrawals")
  public ResponseEntity<TransactionResponseDTO> createWalletByPersonId(
      @PathVariable Long accountNumber,
      @RequestBody TransactionRequestDTO body,
      @RequestHeader(value = "traceId", required = false) String traceId) throws BusinessException {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(transactionService.withdrawalFromAccount(
            accountNumber,
            body,
            resolveTraceId(traceId))
        );
  }

  private String resolveTraceId(String traceId) {
    if(traceId != null && !traceId.isBlank()) {
      return traceId;

    } else {
      return UUID.randomUUID().toString();

    }
  }
}
