package com.mbank.batch.service;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.mapper.TransactionMapper;
import com.mbank.batch.repository.AtmWithdrawalHistoryRepo;
import com.mbank.batch.repository.BillPaymentHistoryRepo;
import com.mbank.batch.repository.FundTransferHistoryRepo;
import com.mbank.batch.spec.TransactionSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class TransactionHistoryService {

  private final AtmWithdrawalHistoryRepo atmWithdrawalHistoryRepo;
  private final BillPaymentHistoryRepo billPaymentHistoryRepo;
  private final FundTransferHistoryRepo fundTransferHistoryRepo;
  private final TransactionMapper transactionMapper;

  public TransactionHistoryService(AtmWithdrawalHistoryRepo atmWithdrawalHistoryRepo,
    BillPaymentHistoryRepo billPaymentHistoryRepo, FundTransferHistoryRepo fundTransferHistoryRepo,
    TransactionMapper transactionMapper) {
    this.atmWithdrawalHistoryRepo = atmWithdrawalHistoryRepo;
    this.billPaymentHistoryRepo = billPaymentHistoryRepo;
    this.fundTransferHistoryRepo = fundTransferHistoryRepo;
    this.transactionMapper = transactionMapper;
  }

  public TransactionHistoryDto updateDescription(String transactionId, String newDescription) {

    var opAtm = this.atmWithdrawalHistoryRepo.findById(transactionId)
      .map(this.transactionMapper::toDto);
    var opBill = this.billPaymentHistoryRepo.findById(transactionId)
      .map(this.transactionMapper::toDto);
    var opFt = this.fundTransferHistoryRepo.findById(transactionId)
      .map(this.transactionMapper::toDto);

    //should be using view, but separated into 3 tables for oop demonstration
    var trx = opAtm
      .orElse(opBill
        .orElse(opFt
          .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity Not Found"))));

    trx.setDescription(newDescription);

    return this.updateHistory(trx);
  }

  public TransactionHistoryDto updateHistory(TransactionHistoryDto dto) {
    try {

      switch (dto.getTransactionType()) {
        case ATM_WITHDRAWAL ->
          this.atmWithdrawalHistoryRepo.save(this.transactionMapper.toAtmEntity(dto));
        case BILL_PAYMENT ->
          this.billPaymentHistoryRepo.save(this.transactionMapper.toBillEntity(dto));
        case FUND_TRANSFER ->
          this.fundTransferHistoryRepo.save(this.transactionMapper.toFtEntity(dto));
      }

      return dto;
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Update Conflict");
    }
  }

  public Page<TransactionHistoryDto> queryTransactions(List<String> accList,
    String customerId, String description, int page, int size) {

    if (page < 0 || size <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Param");
    }

    var pageable = PageRequest.of(page, size);
    return this.queryTransactions(accList, customerId, description, pageable);
  }

  private Page<TransactionHistoryDto> queryTransactions(List<String> accList,
    String customerId, String description, Pageable pageable) {
    if ((accList == null || accList.isEmpty())
      && !StringUtils.hasText(customerId)
      && !StringUtils.hasText(description))
      return Page.empty(pageable);

    var spec1 = TransactionSpecifications.constructAtmQuerySpec(accList, customerId, description);
    var spec2 = TransactionSpecifications.constructBillQuerySpec(accList, customerId, description);
    var spec3 = TransactionSpecifications.constructFtQuerySpec(accList, customerId, description);

    var enList1 = this.atmWithdrawalHistoryRepo.findAll(spec1);
    var enList2 = this.billPaymentHistoryRepo.findAll(spec2);
    var enList3 = this.fundTransferHistoryRepo.findAll(spec3);

    var resList = Stream.of(enList1, enList2, enList3)
      .flatMap(list -> list.stream().map(this.transactionMapper::toDto))
      .toList();

    return this.createPage(resList, pageable);
  }

  public <T> Page<T> createPage(List<T> list, Pageable pageable) {
    int total = list.size();
    int start = (int) pageable.getOffset();

    if (start >= total || start < 0) {
      return new PageImpl<>(List.of(), pageable, total);
    }

    int end = Math.min(start + pageable.getPageSize(), total);
    List<T> pageContent = list.subList(start, end);

    return new PageImpl<>(pageContent, pageable, total);
  }
}
