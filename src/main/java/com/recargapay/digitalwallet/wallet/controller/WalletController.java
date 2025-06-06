package com.recargapay.digitalwallet.wallet.controller;

import com.recargapay.digitalwallet.exceptions.BusinessException;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateRequestDTO;
import com.recargapay.digitalwallet.wallet.dto.WalletCreateResponseDTO;
import com.recargapay.digitalwallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @PostMapping("/{documentNumber}")
  public ResponseEntity<WalletCreateResponseDTO> createWalletByPersonId(
      @PathVariable String documentNumber,
      @RequestBody WalletCreateRequestDTO body) throws BusinessException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(walletService.createWallet(documentNumber, body));
  }
}
