package com.recargapay.digitalwallet.transaction.service;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.transaction.dto.TransactionDTOType;
import com.recargapay.digitalwallet.transaction.dto.TransactionResponseDTO;
import com.recargapay.digitalwallet.transaction.dto.TransactionRequestDTO;
import com.recargapay.digitalwallet.transaction.model.Transaction;
import com.recargapay.digitalwallet.transaction.model.TransactionType;
import com.recargapay.digitalwallet.transaction.repository.TransactionRepository;
import com.recargapay.digitalwallet.wallet.dto.WalletResponseDTO;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import com.recargapay.digitalwallet.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class TransactionService {

  private final ZoneId zoneId;
  private final TransactionRepository transactionRepository;
  private final WalletService walletService;
  private final UUID transactionTracerId;

  public TransactionService(
      @Value("${app.zoneId}") ZoneId zoneId,
      TransactionRepository transactionRepository,
      WalletService walletService) {
    this.zoneId = zoneId;
    this.transactionRepository = transactionRepository;
    this.walletService = walletService;
    this.transactionTracerId = UUID.randomUUID();
  }

  @Transactional
  public TransactionResponseDTO withdrawalFromAccount(
      Long accountNumber,
      TransactionRequestDTO withdrawalRequest) throws BusinessException {
    final var txType = TransactionType.DEBIT;
    final var timestamp = ZonedDateTime.now(zoneId);
    final var wallet = walletService.findWalletByNumber(accountNumber);

    validateWithdrawalOperation(wallet, withdrawalRequest);

    var transaction = executeTransaction(
        wallet.getAccountNumber(),
        withdrawalRequest.amount(),
        txType,
        timestamp,
        transactionTracerId);

    walletService.updateWalletBalance(
        wallet.getAccountNumber(),
        withdrawalRequest.amount(),
        txType);

    return mapNonFullTransferTransactionToResponse(transaction, TransactionDTOType.WITHDRAWAL);
  }

  /**
  @Transactional
  public ResponseEntity<TransactionResponseDTO> depositToAccount(
      Long accountNumber,
      UUID tracerId) throws BusinessException {
    var timestamp = ZonedDateTime.now(zoneId);
    var wallet = walletService.findWalletByNumber(accountNumber);

  }

  @Transactional
  public ResponseEntity<TransactionResponseDTO> transfer(
      Long fromAccountNumber,
      Long toAccountNumber,
      UUID tracerId) throws BusinessException {
    var timestamp = ZonedDateTime.now(zoneId);
    var fromWallet = walletService.findWalletByNumber(fromAccountNumber);
    var toWallet = walletService.findWalletByNumber(fromAccountNumber);

  }
  */

  private static void validateWithdrawalOperation(
      WalletResponseDTO wallet,
      TransactionRequestDTO withdrawalRequest) throws BusinessException {
    if (withdrawalRequest.amount().compareTo(wallet.getBalance().add(wallet.getOverdraftLimit())) > 0) {
      throw new BusinessException("Insufficient funds", HttpStatus.BAD_REQUEST);
    }
  }

  private Transaction executeTransaction(
      Long accountNumber,
      BigDecimal amount,
      TransactionType transactionType,
      ZonedDateTime timestamp,
      UUID tracerId) {

    return transactionRepository.save(Transaction.builder()
        .wallet(Wallet.builder()
            .accountNumber(accountNumber)
            .build())
        .amount(amount)
        .transactionType(transactionType)
        .transactionTime(timestamp)
        .transactionTracerId(tracerId)
        .build()
    );
  }

  private static TransactionResponseDTO mapNonFullTransferTransactionToResponse(
      Transaction transaction,
      TransactionDTOType transactionType) {
    var transactionResponse = TransactionResponseDTO.builder()
        .amount(transaction.getAmount())
        .type(transactionType)
        .transactionTracerId(transaction.getTransactionTracerId())
        .timestamp(transaction.getTransactionTime().toLocalDateTime());

    switch (transactionType) {
      case DEPOSIT -> transactionResponse.toWalletNumber(transaction.getWallet().getAccountNumber());
      case WITHDRAWAL -> transactionResponse.fromWalletNumber(transaction.getWallet().getAccountNumber());
      default -> throw new IllegalArgumentException("Only credit or debit operation types should be passed to non full transfer response mapping method");
    }
    
    return transactionResponse.build();
  }
}
