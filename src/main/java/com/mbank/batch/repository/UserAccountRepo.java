package com.mbank.batch.repository;

import com.mbank.batch.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountRepo extends JpaRepository<UserAccount, String> {

  List<UserAccount> findByAccountNumber(String accountNumber);

}
