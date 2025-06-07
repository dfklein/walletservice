package com.recargapay.digitalwallet.wallet.service;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.person.repository.PersonRepository;
import com.recargapay.digitalwallet.transaction.model.TransactionType;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateRequestDTO;
import com.recargapay.digitalwallet.wallet.dto.WalletResponseDTO;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import com.recargapay.digitalwallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;
  private final PersonRepository personRepository;

  @Transactional(readOnly = true)
  public WalletResponseDTO findBalanceByAccountNumber(Long accountNumber) throws BusinessException {
    var balance = walletRepository.findBalanceByAccountNumber(accountNumber)
        .orElseThrow(() -> new BusinessException("Account not found for given number", HttpStatus.NOT_FOUND));

    return WalletResponseDTO.builder()
        .balance(balance)
        .build();
  }

  @Transactional(readOnly = true)
  public WalletResponseDTO findWalletByNumber(Long accountNumber) throws BusinessException {
    var wallet = walletRepository.findById(accountNumber)
        .orElseThrow(() -> new BusinessException("Account not found for given number", HttpStatus.NOT_FOUND));

    return mapDBToResponse(wallet);
  }

  @Transactional(rollbackFor = { Exception.class })
  public WalletResponseDTO createWallet(
      String documentNumber,
      WalletCreateRequestDTO requestBody) throws BusinessException {
    var person = personRepository.findById(documentNumber)
        .orElseThrow(() -> new BusinessException("Person not found for the given document number", HttpStatus.NOT_FOUND));

    var wallet = walletRepository.save(mapRequestToDB(requestBody, person));

    return mapDBToResponse(wallet);
  }

  @Transactional(rollbackFor = { Exception.class })
  public WalletResponseDTO updateWalletBalance(
      Long accountNumber,
      BigDecimal amount,
      TransactionType transactionType) throws BusinessException {
    var wallet = walletRepository.findById(accountNumber)
        .orElseThrow(() -> new BusinessException("Account not found for given number", HttpStatus.NOT_FOUND));

    switch (transactionType) {
      case CREDIT -> wallet.setBalance(wallet.getBalance().add(amount));
      case DEBIT -> wallet.setBalance(wallet.getBalance().subtract(amount));
    }

    return mapDBToResponse(wallet);

  }

  private static WalletResponseDTO mapDBToResponse(Wallet wallet) {
    return WalletResponseDTO.builder()
        .accountNumber(wallet.getAccountNumber())
        .balance(wallet.getBalance())
        .descriprion(wallet.getDescription())
        .overdraftLimit(wallet.getOverdraftLimit())
        .build();
  }

  private static Wallet mapRequestToDB(WalletCreateRequestDTO dto, Person person) {
    return Wallet.builder()
        .balance(BigDecimal.ZERO)
        .description(dto.description())
        .person(person)
        .build();
  }

}
