package com.mbank.batch.repository;

import com.mbank.batch.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory, String> {

  Page<TransactionHistory> findByTransactionIdAndAccountNumberAndCustomerId(
    String id, String accountNumber, String customerId, Pageable pageable);

  Page<TransactionHistory> findByAccountNumber(String accountNumber, Pageable pageable);

  Page<TransactionHistory> findByCustomerId(String customerId, Pageable pageable);

  Page<TransactionHistory> findByTransactionId(String id, Pageable pageable);

}
