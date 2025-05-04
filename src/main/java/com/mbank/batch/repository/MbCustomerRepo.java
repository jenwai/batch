package com.mbank.batch.repository;

import com.mbank.batch.entity.MbCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MbCustomerRepo extends JpaRepository<MbCustomer, String> {
}
