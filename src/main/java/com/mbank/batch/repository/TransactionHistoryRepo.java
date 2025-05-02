package com.mbank.batch.repository;

import com.mbank.batch.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory, String>,
  JpaSpecificationExecutor<TransactionHistory> {

}
