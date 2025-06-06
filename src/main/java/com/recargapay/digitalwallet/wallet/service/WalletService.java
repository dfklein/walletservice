package com.recargapay.digitalwallet.wallet.service;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.person.repository.PersonRepository;
import com.recargapay.digitalwallet.wallet.dto.WalletBalanceResponseDTO;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateRequestDTO;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateResponseDTO;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import com.recargapay.digitalwallet.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;
  private final PersonRepository personRepository;

  public WalletBalanceResponseDTO getWalletBalance(Long accountNumber) throws BusinessException {
    var wallet = walletRepository.findById(accountNumber)
        .orElseThrow(() -> new BusinessException("Account not found for given number", HttpStatus.NOT_FOUND));

    return mapDBToBalanceRetrievalResponse(wallet);
  }

  @Transactional
  public WalletCreateResponseDTO createWallet(
      String documentNumber,
      WalletCreateRequestDTO requestBody) throws BusinessException {
    var person = personRepository.findById(documentNumber)
        .orElseThrow(() -> new BusinessException("Person not found for the given document number", HttpStatus.NOT_FOUND));

    var wallet = walletRepository.save(mapFromCreateRequestToDB(requestBody, person));

    return mapDBToCreateResponse(wallet);
  }

  private static WalletCreateResponseDTO mapDBToCreateResponse(Wallet wallet) {
    return WalletCreateResponseDTO.builder()
        .accountNumber(wallet.getAccountNumber())
        .balance(wallet.getBalance())
        .descriprion(wallet.getDescription())
        .build();
  }

  private static WalletBalanceResponseDTO mapDBToBalanceRetrievalResponse(Wallet wallet) {
    return WalletBalanceResponseDTO.builder()
        .balance(wallet.getBalance())
        .build();
  }

  private static Wallet mapFromCreateRequestToDB(WalletCreateRequestDTO dto, Person person) {
    return Wallet.builder()
        .balance(BigDecimal.ZERO)
        .description(dto.description())
        .person(person)
        .build();
  }

}
