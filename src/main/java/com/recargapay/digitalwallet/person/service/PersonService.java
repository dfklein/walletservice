package com.recargapay.digitalwallet.person.service;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.person.dto.PersonResponseDTO;
import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;

  @Transactional(readOnly = true)
  public PersonResponseDTO findPersonByDocumentNumber(String documentNumber) throws BusinessException {
    return personRepository.findById(documentNumber)
        .map(PersonService::mapToResponse)
        .orElseThrow(
            () -> new BusinessException("No person found for given document number",
                HttpStatus.NOT_FOUND)
        );
  }

  private static PersonResponseDTO mapToResponse(Person person) {
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
