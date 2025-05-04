package com.mbank.batch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FUND_TRANSFER_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FundTransferHistory extends TransactionHistory {

  @Id
  @Column(name = "TRANSACTION_ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String transactionId;

  @Column(name = "INTRA_BANK")
  private Boolean intraBank;

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID", nullable = false)
  private MbCustomer customer;

  @ManyToOne
  @JoinColumn(name = "ACCOUNT_ID", nullable = false)
  private UserAccount account;
}
