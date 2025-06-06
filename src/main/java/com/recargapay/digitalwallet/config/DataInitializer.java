package com.recargapay.digitalwallet.config;

import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.person.repository.PersonRepository;
import com.recargapay.digitalwallet.wallet.model.Wallet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

  private final PersonRepository personRepository;

  @PostConstruct
  public void init() {
    var person = Person.builder()
        .documentNumber("11122233344")
        .firstName("Pedro")
        .lastName("Silva")
        .email("psilva@gmail.com")
        .build();

    var wallet1 = Wallet.builder()
        .balance(BigDecimal.ZERO)
        .description("my first wallet")
        .person(person)
        .build();

    var wallet2 = Wallet.builder()
        .balance(BigDecimal.ZERO)
        .description("a second wallet for me!")
        .person(person)
        .build();

    person.setWallets(List.of(wallet1, wallet2));

    var person2 = Person.builder()
        .documentNumber("55566677788")
        .firstName("Joao")
        .lastName("Pereira")
        .email("jpereira@gmail.com")
        .build();

    personRepository.save(person);
    personRepository.save(person2);
  }
}
