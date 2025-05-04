package com.mbank.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MB_CUSTOMER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MbCustomer {

  @Id
  @Column(name = "CUSTOMER_ID", unique = true, nullable = false)
  private String customerId;
}
