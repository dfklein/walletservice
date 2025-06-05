package com.recargapay.wallet.person.service;

import com.recargapay.wallet.person.dto.PersonResponseDTO;
import com.recargapay.wallet.person.repository.Person;
import com.recargapay.wallet.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;

  public Optional<PersonResponseDTO> getPersonByDocumentNumber(String documentNumber) {
    return personRepository.findByDocumentNumber(documentNumber)
        .map(this::mapToDTO);
  }

  private PersonResponseDTO mapToDTO(Person person) {
    return PersonResponseDTO.builder()
        .fullName(
            person.getFirstName()
                .concat(" ")
                .concat(person.getLastName())
        )
        .documentNumber(person.getDocumentNumber())
        .build();
  }
}
