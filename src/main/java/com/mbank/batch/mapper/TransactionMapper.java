package com.mbank.batch.mapper;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.AtmWithdrawalHistory;
import com.mbank.batch.entity.BillPaymentHistory;
import com.mbank.batch.entity.FundTransferHistory;
import com.mbank.batch.entity.TransactionHistory;
import com.mbank.batch.model.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(target = "transactionType", expression = "java(constructTransactionType(en))")
  TransactionHistoryDto toDto(TransactionHistory en);

  AtmWithdrawalHistory toAtmEntity(TransactionHistoryDto dto);

  BillPaymentHistory toBillEntity(TransactionHistoryDto dto);

  FundTransferHistory toFtEntity(TransactionHistoryDto dto);

  default TransactionType constructTransactionType(TransactionHistory en) {
    if (en instanceof AtmWithdrawalHistory) {
      return TransactionType.ATM_WITHDRAWAL;
    } else if (en instanceof BillPaymentHistory) {
      return TransactionType.BILL_PAYMENT;
    } else if (en instanceof FundTransferHistory) {
      return TransactionType.FUND_TRANSFER;
    } else {
      return TransactionType.OTHERS;
    }
  }
}
