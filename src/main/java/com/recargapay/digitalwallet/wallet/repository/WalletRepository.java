package com.recargapay.digitalwallet.wallet.repository;

import com.recargapay.digitalwallet.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT w.balance FROM Wallet w WHERE w.accountNumber = :accountNumber")
    Optional<BigDecimal> findBalanceByAccountNumber(@Param("accountNumber") Long accountNumber);

}
