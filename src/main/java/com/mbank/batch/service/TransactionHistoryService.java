package com.mbank.batch.service;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.TransactionHistory;
import com.mbank.batch.mapper.TransactionMapper;
import com.mbank.batch.repository.TransactionHistoryRepo;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionHistoryService {

  private final TransactionHistoryRepo transactionHistoryRepo;
  private final TransactionMapper transactionMapper;

  public TransactionHistoryService(TransactionHistoryRepo transactionHistoryRepo,
    TransactionMapper transactionMapper) {
    this.transactionHistoryRepo = transactionHistoryRepo;
    this.transactionMapper = transactionMapper;
  }

  public TransactionHistoryDto updateDescription(String transactionId, String newDescription) {

    var trx = this.transactionHistoryRepo.findById(transactionId)
      .map(this.transactionMapper::toDto)
      .orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity Not Found"));

    trx.setDescription(newDescription);

    return this.updateHistory(trx);
  }

  public TransactionHistoryDto updateHistory(TransactionHistoryDto dto) {
    try {
      this.transactionHistoryRepo.save(this.transactionMapper.toEntity(dto));
      return dto;
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Update Conflict");
    }
  }

  public Page<TransactionHistoryDto> queryTransactions(String id, String accountNumber,
    String customerId, int page, int size) {

    if (page < 0 || size <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Param");
    }

    var pageable = PageRequest.of(page, size);
    return this.queryTransactions(id, accountNumber, customerId, pageable)
      .map(this.transactionMapper::toDto);
  }

  private Page<TransactionHistory> queryTransactions(String id, String accountNumber,
    String customerId, Pageable pageable) {
    if (id != null && accountNumber != null && customerId != null) {
      return this.transactionHistoryRepo.findByTransactionIdAndAccountNumberAndCustomerId(id,
        accountNumber, customerId, pageable);
    } else if (accountNumber != null) {
      return this.transactionHistoryRepo.findByAccountNumber(accountNumber, pageable);
    } else if (customerId != null) {
      return this.transactionHistoryRepo.findByCustomerId(customerId, pageable);
    } else if (id != null) {
      return this.transactionHistoryRepo.findByTransactionId(id, pageable);
    }
    return Page.empty(pageable);
  }
}
