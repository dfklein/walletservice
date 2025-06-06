package com.recargapay.digitalwallet.wallet.service;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.person.repository.PersonRepository;
import com.recargapay.digitalwallet.person.service.PersonService;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateRequestDTO;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateResponseDTO;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import com.recargapay.digitalwallet.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;
  private final PersonRepository personRepository;

  @Transactional
  public WalletCreateResponseDTO createWallet(
      String personId,
      WalletCreateRequestDTO requestBody) throws BusinessException {
    var person = personRepository.findById(personId)
        .orElseThrow(() -> new BusinessException("Person not found for the given document number", HttpStatus.NOT_FOUND));

    var wallet = walletRepository.save(mapToDB(requestBody, person));

    return mapToResponse(wallet);
  }

  private static WalletCreateResponseDTO mapToResponse(Wallet wallet) {
    return WalletCreateResponseDTO.builder()
        .number(wallet.getNumber())
        .balance(wallet.getBalance())
        .descriprion(wallet.getDescription())
        .build();
  }

  private static Wallet mapToDB(WalletCreateRequestDTO dto, Person person) {
    return Wallet.builder()
        .balance(BigDecimal.ZERO)
        .description(dto.description())
        .person(person)
        .build();
  }
}
