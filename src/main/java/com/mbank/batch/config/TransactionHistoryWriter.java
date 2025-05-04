package com.mbank.batch.config;

import com.mbank.batch.entity.TransactionHistory;
import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.*;
import com.mbank.batch.model.TransactionType;
import com.mbank.batch.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@StepScope
@Slf4j
public class TransactionHistoryWriter implements ItemWriter<TransactionHistoryDto> {

  private final AtmWithdrawalHistoryRepo atmWithdrawalHistoryRepo;
  private final BillPaymentHistoryRepo billPaymentHistoryRepo;
  private final FundTransferHistoryRepo fundTransferHistoryRepo;
  private final UserAccountRepo userAccountRepo;
  private final MbCustomerRepo mbCustomerRepo;

  public TransactionHistoryWriter(AtmWithdrawalHistoryRepo atmWithdrawalHistoryRepo,
    BillPaymentHistoryRepo billPaymentHistoryRepo, FundTransferHistoryRepo fundTransferHistoryRepo,
    UserAccountRepo userAccountRepo, MbCustomerRepo mbCustomerRepo) {
    this.atmWithdrawalHistoryRepo = atmWithdrawalHistoryRepo;
    this.billPaymentHistoryRepo = billPaymentHistoryRepo;
    this.fundTransferHistoryRepo = fundTransferHistoryRepo;
    this.userAccountRepo = userAccountRepo;
    this.mbCustomerRepo = mbCustomerRepo;
  }

  @Override
  public void write(Chunk<? extends TransactionHistoryDto> items) {

    log.info("SAVING " + items.size() + " rows into transaction history");

    for (TransactionHistoryDto trx : items) {

      var cust = this.mbCustomerRepo.findById(trx.getCustomerId())
        .orElse(this.mbCustomerRepo.save(
          MbCustomer.builder()
            .customerId(trx.getCustomerId())
            .build()
        ));
      var acc = this.userAccountRepo.findByAccountNumber(trx.getAccountNumber())
        .stream()
        .findFirst()
        .orElse(this.userAccountRepo.save(
          UserAccount.builder()
            .customer(cust)
            .accountNumber(trx.getAccountNumber())
            .build()
        ));

      var description = trx.getDescription();
      if (TransactionType.ATM_WITHDRAWAL.getDescription().equalsIgnoreCase(description)) {
        var tx = new AtmWithdrawalHistory();
        this.fillCommonTransactionFields(tx, trx, cust, acc);
        this.atmWithdrawalHistoryRepo.save(tx);
      } else if (TransactionType.BILL_PAYMENT.getDescription().equalsIgnoreCase(description)) {
        var tx = new BillPaymentHistory();
        this.fillCommonTransactionFields(tx, trx, cust, acc);
        this.billPaymentHistoryRepo.save(tx);
      } else if (TransactionType.FUND_TRANSFER.getDescription().equalsIgnoreCase(description)) {
        var tx = new FundTransferHistory();
        this.fillCommonTransactionFields(tx, trx, cust, acc);
        tx.setIntraBank(TransactionType.isIntraFundTransfer(description));
        this.fundTransferHistoryRepo.save(tx);
      }
    }
  }

  private void fillCommonTransactionFields(TransactionHistory tx, TransactionHistoryDto dto,
    MbCustomer customer, UserAccount account) {
    tx.setTrxAmount(dto.getTrxAmount());
    tx.setTrxDate(dto.getTrxDate());
    tx.setTrxTime(dto.getTrxTime());
    tx.setDescription(dto.getDescription());
    tx.setCustomer(customer);
    tx.setAccount(account);
  }
}
