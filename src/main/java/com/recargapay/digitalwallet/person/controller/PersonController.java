package com.recargapay.digitalwallet.person.controller;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.person.dto.PersonResponseDTO;
import com.recargapay.digitalwallet.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

  private final PersonService personService;

  @GetMapping("/{documentNumber}")
  public ResponseEntity<PersonResponseDTO> findPersonByDocumentNumber(
      @PathVariable String documentNumber) throws BusinessException {
    return ResponseEntity.ok(personService.findPersonByDocumentNumber(documentNumber));
  }
}
