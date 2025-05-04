package com.mbank.batch.repository;

import com.mbank.batch.entity.FundTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FundTransferHistoryRepo extends JpaRepository<FundTransferHistory, String>,
  JpaSpecificationExecutor<FundTransferHistory> {

}

