package com.recargapay.digitalwallet.audit;

import com.recargapay.digitalwallet.transaction.model.Transaction;
import com.recargapay.digitalwallet.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public AuditLog registerTransactionOperationLog(
      TransactionType transactionType,
      Long accountNumber,
      BigDecimal amount,
      ZonedDateTime operationTimestamp,
      String traceId,
      OperationStatus status,
      String message) {
    return auditLogRepository.save(AuditLog.builder()
        .transactionType(transactionType)
        .accountNumber(accountNumber)
        .amount(amount)
        .timestamp(operationTimestamp)
        .traceId(traceId)
        .status(status)
        .message(message)
        .build()
    );
  }
}
