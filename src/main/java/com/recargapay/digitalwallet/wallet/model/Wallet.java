package com.recargapay.digitalwallet.wallet.model;

import com.recargapay.digitalwallet.person.model.Person;
import com.recargapay.digitalwallet.transaction.model.Transaction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  private Long accountNumber;

  @ManyToOne(optional = false)
  @JoinColumn(name = "person_id", nullable = false)
  private Person person;

  @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Transaction> transactions = new ArrayList<>();

  @Column(nullable = false, columnDefinition = "decimal(10,2) default 0.00")
  private BigDecimal balance;

  @Column(nullable = false, columnDefinition = "decimal(10,2) default 0.00")
  private BigDecimal overdraftLimit;

  private String description;
}
