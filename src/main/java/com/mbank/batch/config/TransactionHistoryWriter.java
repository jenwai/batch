package com.mbank.batch.config;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.TransactionHistory;
import com.mbank.batch.repository.TransactionHistoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@StepScope
@Slf4j
public class TransactionHistoryWriter implements ItemWriter<TransactionHistoryDto> {

  private final TransactionHistoryRepo historyRepo;

  public TransactionHistoryWriter(TransactionHistoryRepo historyRepo) {
    this.historyRepo = historyRepo;
  }

  @Override
  public void write(Chunk<? extends TransactionHistoryDto> items) {
    var historyEnList = new ArrayList<TransactionHistory>();

    for (TransactionHistoryDto trx : items) {
      var trxEn = TransactionHistory.builder()
        .trxTime(trx.getTrxTime())
        .trxDate(trx.getTrxDate())
        .trxAmount(trx.getTrxAmount())
        .description(trx.getDescription())
        .accountNumber(trx.getAccountNumber())
        .customerId(trx.getCustomerId())
        .build();
      historyEnList.add(trxEn);
    }

    log.info("SAVING " + historyEnList.size() + " rows into transaction history");
    historyRepo.saveAll(historyEnList);
  }
}
