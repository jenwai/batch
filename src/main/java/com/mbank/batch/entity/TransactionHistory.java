package com.mbank.batch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "TRANSACTION_HISTORY")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TRANSACTION_ID")
  private String transactionId;

  @Column(name = "TRX_AMOUNT")
  private BigDecimal trxAmount;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "TRX_DATE")
  private LocalDate trxDate;

  @Column(name = "TRX_TIME")
  private LocalTime trxTime;

  @Column(name = "CUSTOMER_ID")
  private String customerId;

  @Column(name = "ACCOUNT_NUMBER")
  private String accountNumber;

  @Version
  @Column(name = "VERSION")
  private Long version;
}
