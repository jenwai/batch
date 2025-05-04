package com.mbank.batch.entity;

import com.mbank.batch.entity.MbCustomer;
import com.mbank.batch.entity.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class TransactionHistory {

  @Column(name = "TRX_AMOUNT")
  private BigDecimal trxAmount;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "TRX_DATE")
  private LocalDate trxDate;

  @Column(name = "TRX_TIME")
  private LocalTime trxTime;

  @Version
  @Column(name = "VERSION")
  private Long version;

  public abstract String getTransactionId();

  public abstract MbCustomer getCustomer();

  public abstract void setCustomer(MbCustomer customer);

  public abstract UserAccount getAccount();

  public abstract void setAccount(UserAccount account);
}
