package com.recargapay.wallet.person.controller;

import com.recargapay.wallet.person.dto.PersonResponseDTO;
import com.recargapay.wallet.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

  private final PersonService personService;

  @GetMapping("/{documentNumber}")
  public ResponseEntity<PersonResponseDTO> getPersonByDocumentNumber(@PathVariable String documentNumber) {
    return personService.getPersonByDocumentNumber(documentNumber)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
