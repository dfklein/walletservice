package com.recargapay.digitalwallet.audit;

import com.recargapay.digitalwallet.transaction.model.TransactionType;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String traceId;

  private String message;

  @Column(nullable = false)
  private TransactionType transactionType;

  @Column(nullable = false)
  private Long accountNumber;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private ZonedDateTime timestamp;

  @Column(nullable = false)
  private OperationStatus status;
}
