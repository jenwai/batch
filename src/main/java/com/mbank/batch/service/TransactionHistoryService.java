package com.mbank.batch.service;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.TransactionHistory;
import com.mbank.batch.mapper.TransactionMapper;
import com.mbank.batch.repository.TransactionHistoryRepo;
import com.mbank.batch.spec.TransactionSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
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

  public Page<TransactionHistoryDto> queryTransactions(List<String> accList,
    String customerId, String description, int page, int size) {

    if (page < 0 || size <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Param");
    }

    var pageable = PageRequest.of(page, size);
    return this.queryTransactions(accList, customerId, description, pageable)
      .map(this.transactionMapper::toDto);
  }

  private Page<TransactionHistory> queryTransactions(List<String> accList,
    String customerId, String description, Pageable pageable) {
    if ((accList == null || accList.isEmpty())
      && !StringUtils.hasText(customerId)
      && !StringUtils.hasText(description))
      return Page.empty(pageable);

    var spec = TransactionSpecifications.constructQuerySpec(accList, customerId, description);
    return this.transactionHistoryRepo.findAll(spec, pageable);
  }
}
