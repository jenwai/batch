package com.mbank.batch.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record TransactionRecord(String accountNumber, BigDecimal trxAmount,
  String description, LocalDate trxDate, LocalTime trxTime, String customerId) {
}
