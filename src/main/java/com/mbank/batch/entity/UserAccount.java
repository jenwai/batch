package com.mbank.batch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_ACCOUNT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

  @Id
  @Column(name = "ACCOUNT_ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String accountId;

  @Column(name = "ACCOUNT_NUMBER")
  private String accountNumber;

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID")
  private MbCustomer customer;
}
