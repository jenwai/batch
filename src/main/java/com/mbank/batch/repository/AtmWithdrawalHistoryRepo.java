package com.mbank.batch.repository;

import com.mbank.batch.entity.AtmWithdrawalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AtmWithdrawalHistoryRepo extends JpaRepository<AtmWithdrawalHistory, String>,
  JpaSpecificationExecutor<AtmWithdrawalHistory> {

}

