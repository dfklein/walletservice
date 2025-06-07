package com.recargapay.digitalwallet.transaction.service;

import com.recargapay.digitalwallet.audit.AuditLogService;
import com.recargapay.digitalwallet.audit.OperationStatus;
import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.transaction.dto.TransactionDTOType;
import com.recargapay.digitalwallet.transaction.dto.TransactionResponseDTO;
import com.recargapay.digitalwallet.transaction.model.Transaction;
import com.recargapay.digitalwallet.transaction.model.TransactionType;
import com.recargapay.digitalwallet.transaction.repository.TransactionRepository;
import com.recargapay.digitalwallet.wallet.dto.WalletResponseDTO;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import com.recargapay.digitalwallet.wallet.service.WalletService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final WalletService walletService;
  private final AuditLogService auditLogService;

  private final ZoneId zoneId;

  public TransactionService(
      @Value("${app.zoneId}") ZoneId zoneId,
      TransactionRepository transactionRepository,
      WalletService walletService,
      AuditLogService auditLogService) {
    this.zoneId = zoneId;
    this.transactionRepository = transactionRepository;
    this.walletService = walletService;
    this.auditLogService = auditLogService;
  }

  @Transactional(readOnly = true)
  public List<TransactionResponseDTO> getTransactionsForAccount(
      Long accountNumber,
      LocalDate from,
      LocalDate to) {

    List<Transaction> transactions;

    if (from != null && to != null) {
      ZonedDateTime start = from.atStartOfDay(ZoneId.systemDefault());
      ZonedDateTime end = to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
      transactions = transactionRepository.findTransactionsByAccountNumberAndPeriod(accountNumber, start, end);

    } else if (from != null) {
      ZonedDateTime start = from.atStartOfDay(ZoneId.systemDefault());
      transactions = transactionRepository.findTransactionsByAccountNumberFrom(accountNumber, start);

    } else if (to != null) {
      ZonedDateTime end = to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
      transactions = transactionRepository.findTransactionsByAccountNumberUntil(accountNumber, end);

    } else {
      transactions = transactionRepository.findTransactionsByAccountNumber(accountNumber);
    }

    return transactions.stream()
        .map(tr -> TransactionResponseDTO.builder()
            .id(tr.getId().toString())
            .type(resolveResponseTransactionType(tr.getTransactionType(), tr.getTransferReferenceId()))
            .amount(tr.getAmount())
            .timestamp(tr.getTransactionTime().toLocalDateTime())
            .build())
        .toList();
  }


  @Transactional(rollbackFor = { Exception.class })
  public TransactionResponseDTO withdrawalFromAccount(
      Long accountNumber,
      BigDecimal amount,
      String traceId) throws Exception {
    final var operationTimestamp = ZonedDateTime.now(zoneId);

    try {
      var transaction = executeOneWayTransaction(
          accountNumber,
          TransactionType.DEBIT,
          amount,
          null,
          operationTimestamp,
          traceId);

      var response = mapNonFullTransferTransactionToResponse(transaction);

      auditLogService.registerTransactionOperationLog(
          accountNumber,
          TransactionType.DEBIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.SUCCESS,
          null
      );

      return response;
    } catch (Exception e) {
      auditLogService.registerTransactionOperationLog(
          accountNumber,
          TransactionType.DEBIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.FAILURE,
          e.getMessage()
      );

      throw e;
    }
  }

  @Transactional(rollbackFor = { Exception.class })
  public TransactionResponseDTO depositToAccount(
      Long accountNumber,
      BigDecimal amount,
      String traceId) throws Exception {
    final var operationTimestamp = ZonedDateTime.now(zoneId);

    try {
      var transaction = executeOneWayTransaction(
          accountNumber,
          TransactionType.CREDIT,
          amount,
          null,
          operationTimestamp,
          traceId);

      var response = mapNonFullTransferTransactionToResponse(transaction);

      auditLogService.registerTransactionOperationLog(
          accountNumber,
          TransactionType.CREDIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.SUCCESS,
          null
      );

      return response;
    } catch (Exception e) {
      auditLogService.registerTransactionOperationLog(
          accountNumber,
          TransactionType.DEBIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.FAILURE,
          e.getMessage()
      );
      throw e;
    }
  }

  @Transactional(rollbackFor = { Exception.class })
  public TransactionResponseDTO transfer(
      Long fromAccountNumber,
      Long toAccountNumber,
      BigDecimal amount,
      String traceId) throws Exception {
    final var transferReferenceId = UUID.randomUUID();
    final var operationTimestamp = ZonedDateTime.now(zoneId);

    try {
      var transactionFrom = executeOneWayTransaction(
          fromAccountNumber,
          TransactionType.DEBIT,
          amount,
          transferReferenceId,
          operationTimestamp,
          traceId);

      var transactionTo = executeOneWayTransaction(
          toAccountNumber,
          TransactionType.CREDIT,
          amount,
          transferReferenceId,
          operationTimestamp,
          traceId);

      var response = mapFullTransferTransactionToResponse(transactionFrom, transactionTo);

      auditLogService.registerTransactionOperationLog(
          fromAccountNumber,
          TransactionType.DEBIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.SUCCESS,
          null
      );

      auditLogService.registerTransactionOperationLog(
          toAccountNumber,
          TransactionType.CREDIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.SUCCESS,
          null
      );

      return response;

    } catch (Exception e) {
      auditLogService.registerTransactionOperationLog(
          fromAccountNumber,
          TransactionType.DEBIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.FAILURE,
          e.getMessage()
      );

      auditLogService.registerTransactionOperationLog(
          toAccountNumber,
          TransactionType.CREDIT,
          amount,
          operationTimestamp,
          traceId,
          OperationStatus.FAILURE,
          e.getMessage()
      );
      throw e;

    }
  }

  private Transaction executeOneWayTransaction(
      Long accountNumber,
      TransactionType transactionType,
      BigDecimal amount,
      @Nullable UUID transferReferenceId,
      ZonedDateTime operationTimestamp,
      String traceId) throws BusinessException {
      final var wallet = walletService.findWalletByNumber(accountNumber);

      if(TransactionType.DEBIT == transactionType) {
        validateWithdrawalOperation(wallet, amount);
      }

      var transaction = executeTransaction(
          wallet.getAccountNumber(),
          amount,
          transactionType,
          transferReferenceId,
          operationTimestamp,
          traceId);

      walletService.updateWalletBalance(
          wallet.getAccountNumber(),
          amount,
          transactionType);

      return transaction;
  }

  private static void validateWithdrawalOperation(
      WalletResponseDTO wallet,
      BigDecimal amount) throws BusinessException {
    if (amount.compareTo(wallet.getBalance().add(wallet.getOverdraftLimit())) > 0) {
      throw new BusinessException("Insufficient funds", HttpStatus.BAD_REQUEST);
    }
  }

  private static TransactionDTOType resolveResponseTransactionType(
      TransactionType transactionType,
      UUID transferReferenceId) {
    if(transferReferenceId == null) {
      if(TransactionType.CREDIT == transactionType) {
        return TransactionDTOType.DEPOSIT;
      }
      return TransactionDTOType.WITHDRAWAL;

    } else {
      return TransactionDTOType.TRANSFER;
    }
  }

  private Transaction executeTransaction(
      Long accountNumber,
      BigDecimal amount,
      TransactionType transactionType,
      UUID transferReferenceId,
      ZonedDateTime timestamp,
      String tracerId) {

    return transactionRepository.save(Transaction.builder()
        .wallet(Wallet.builder()
            .accountNumber(accountNumber)
            .build())
        .amount(amount)
        .transactionType(transactionType)
        .transferReferenceId(transferReferenceId)
        .transactionTime(timestamp)
        .transactionTracerId(tracerId)
        .build()
    );
  }

  private static TransactionResponseDTO mapNonFullTransferTransactionToResponse(Transaction transaction) {
    var transactionType = resolveResponseTransactionType(transaction.getTransactionType(), transaction.getTransferReferenceId());

    var transactionResponse = TransactionResponseDTO.builder()
        .amount(transaction.getAmount())
        .type(transactionType)
        .id(transaction.getId().toString())
        .timestamp(transaction.getTransactionTime().toLocalDateTime());

    switch (transactionType) {
      case DEPOSIT -> transactionResponse.toWalletNumber(transaction.getWallet().getAccountNumber());
      case WITHDRAWAL -> transactionResponse.fromWalletNumber(transaction.getWallet().getAccountNumber());
      default -> throw new IllegalArgumentException("Only credit or debit operation types should be passed to non full transfer response mapping method");
    }
    
    return transactionResponse.build();
  }

  private TransactionResponseDTO mapFullTransferTransactionToResponse(Transaction transactionFrom, Transaction transactionTo) {
    return TransactionResponseDTO.builder()
        .amount(transactionFrom.getAmount())
        .type(TransactionDTOType.TRANSFER)
        .fromWalletNumber(transactionFrom.getWallet().getAccountNumber())
        .toWalletNumber(transactionTo.getWallet().getAccountNumber())
        .id(transactionFrom.getId().toString())
        .timestamp(transactionFrom.getTransactionTime().toLocalDateTime())
        .build();
  }
}
