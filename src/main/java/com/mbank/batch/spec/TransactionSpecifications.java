package com.mbank.batch.spec;

import com.mbank.batch.entity.TransactionHistory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public class TransactionSpecifications {

  private TransactionSpecifications() {
  }

  public static Specification<TransactionHistory> constructQuerySpec(List<String> accList,
    String custId, String description) {
    return (Root<TransactionHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      Predicate combined = null;

      if (accList != null && !accList.isEmpty()) {
        var p = cb.in(root.get("accountNumber"));
        for (var a : accList) {
          p.value(a);
        }
        combined = p;
      }
      if (StringUtils.hasText(custId)) {
        var p = cb.equal(root.get("customerId"), custId);
        combined = (combined == null) ? p : cb.or(combined, p);
      }
      if (StringUtils.hasText(description)) {
        var p = cb.like(
          cb.lower(root.get("description")),
          "%" + description.toLowerCase() + "%"
        );
        combined = (combined == null) ? p : cb.or(combined, p);
      }

      if (combined == null) {
        return cb.disjunction();
      }

      return combined;
    };
  }
}
