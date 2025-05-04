package com.mbank.batch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BILL_PAYMENT_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BillPaymentHistory extends TransactionHistory {

  @Id
  @Column(name = "TRANSACTION_ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String transactionId;

  @Column(name = "MERCHANT_ID")
  private String merchantId;

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID", nullable = false)
  private MbCustomer customer;

  @ManyToOne
  @JoinColumn(name = "ACCOUNT_ID", nullable = false)
  private UserAccount account;
}
