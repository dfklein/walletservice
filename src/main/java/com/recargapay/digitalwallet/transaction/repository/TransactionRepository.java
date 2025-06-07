package com.recargapay.digitalwallet.transaction.repository;

import com.recargapay.digitalwallet.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

  @Query("""
    SELECT t FROM Transaction t
    WHERE t.wallet.accountNumber = :accountNumber
      AND t.transactionTime BETWEEN :start AND :end
  """)
  List<Transaction> findTransactionsByAccountNumberAndPeriod(
      @Param("accountNumber") Long accountNumber,
      @Param("start") ZonedDateTime start,
      @Param("end") ZonedDateTime end
  );


  @Query("""
    SELECT t FROM Transaction t
    WHERE t.wallet.accountNumber = :accountNumber
      AND t.transactionTime >= :start
  """)
  List<Transaction> findTransactionsByAccountNumberFrom(
      @Param("accountNumber") Long accountNumber,
      @Param("start") ZonedDateTime start
  );

  @Query("""
    SELECT t FROM Transaction t
    WHERE t.wallet.accountNumber = :accountNumber
      AND t.transactionTime <= :end
  """)
  List<Transaction> findTransactionsByAccountNumberUntil(
      @Param("accountNumber") Long accountNumber,
      @Param("end") ZonedDateTime end
  );

  @Query("""
    SELECT t FROM Transaction t
    WHERE t.wallet.accountNumber = :accountNumber
""")
  List<Transaction> findTransactionsByAccountNumber(
      @Param("accountNumber") Long accountNumber
  );
}
