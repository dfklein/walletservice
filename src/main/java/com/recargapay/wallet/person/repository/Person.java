package com.recargapay.wallet.person.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, nullable = false)
  private UUID id;

  private String firstName;

  private String lastName;

  private String documentNumber;

}
