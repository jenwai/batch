package com.mbank.batch.repository;

import com.mbank.batch.entity.BillPaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BillPaymentHistoryRepo extends JpaRepository<BillPaymentHistory, String>,
  JpaSpecificationExecutor<BillPaymentHistory> {

}

