package com.mbank.batch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {


  FUND_TRANSFER("FUND TRANSFER"),
  BILL_PAYMENT("BILL PAYMENT"),
  ATM_WITHDRAWAL("ATM WITHDRWAL"),
  OTHERS("OTHERS");

  private final String description;

  public static boolean isIntraFundTransfer(String description) {
    return description.contains("3rd Party FUND TRANSFER");
  }
}
