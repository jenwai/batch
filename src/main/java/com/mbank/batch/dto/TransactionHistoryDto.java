package com.mbank.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {

  private String transactionId;

  private BigDecimal trxAmount;

  private String description;

  private LocalDate trxDate;

  private LocalTime trxTime;

  private String customerId;

  private String accountNumber;

  private Long version;
}
