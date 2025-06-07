package com.recargapay.digitalwallet.transaction.controller;

import com.recargapay.digitalwallet.transaction.dto.TransactionRequestDTO;
import com.recargapay.digitalwallet.transaction.dto.TransactionResponseDTO;
import com.recargapay.digitalwallet.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping("/wallets/{accountNumber}/transactions")
  public ResponseEntity<List<TransactionResponseDTO>> listTransactions(
      @PathVariable Long accountNumber,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    List<TransactionResponseDTO> transactions = transactionService.getTransactionsForAccount(accountNumber, from, to);
    return ResponseEntity.ok(transactions);
  }


  @PostMapping("/wallets/{accountNumber}/withdrawals")
  public ResponseEntity<TransactionResponseDTO> withdrawalFromAccount(
      @PathVariable Long accountNumber,
      @RequestBody TransactionRequestDTO body,
      @RequestHeader(value = "traceId", required = false) String traceId) throws Exception {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(transactionService.withdrawalFromAccount(
            accountNumber,
            body.amount(),
            resolveTraceId(traceId))
        );
  }

  @PostMapping("/wallets/{accountNumber}/deposits")
  public ResponseEntity<TransactionResponseDTO> depositToAccount(
      @PathVariable Long accountNumber,
      @RequestBody TransactionRequestDTO body,
      @RequestHeader(value = "traceId", required = false) String traceId) throws Exception {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(transactionService.depositToAccount(
            accountNumber,
            body.amount(),
            resolveTraceId(traceId))
        );
  }

  @PostMapping("/wallets/{sourceAccountNumber}/transfers")
  public ResponseEntity<TransactionResponseDTO> transferToAccount(
      @PathVariable Long sourceAccountNumber,
      @RequestBody TransactionRequestDTO body,
      @RequestHeader(value = "traceId", required = false) String traceId) throws Exception {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(transactionService.transfer(
            sourceAccountNumber,
            body.destinationAccountNumber(),
            body.amount(),
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
