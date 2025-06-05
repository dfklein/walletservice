package com.recargapay.wallet.config;

import com.recargapay.wallet.person.repository.Person;
import com.recargapay.wallet.person.repository.PersonRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

  private final PersonRepository personRepository;

  @PostConstruct
  public void init() {
    var testPerson = Person.builder()
        .documentNumber("11122233344")
        .firstName("Pedro")
        .lastName("Silva")
        .build();

    personRepository.save(testPerson);
  }
}
