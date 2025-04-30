package com.mbank.batch.controller;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.model.UpdateTrxDescApiRq;
import com.mbank.batch.service.TransactionHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionHistoryController {

  private final TransactionHistoryService transactionHistoryService;

  public TransactionHistoryController(TransactionHistoryService transactionHistoryService) {
    this.transactionHistoryService = transactionHistoryService;
  }

  @PatchMapping("/{id}/description")
  public ResponseEntity<?> updateTrxDesc(@PathVariable String id,
    @RequestBody UpdateTrxDescApiRq apiRq) {

    try {
      return ResponseEntity.ok(
        this.transactionHistoryService.updateDescription(id, apiRq.getDescription()));
    } catch (ResponseStatusException e) {
      return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }
  }

  @GetMapping("")
  public ResponseEntity<Page<TransactionHistoryDto>> queryTransactions(
    @RequestParam(value = "id", required = false) String id,
    @RequestParam(value = "acc_no", required = false) String accountNumber,
    @RequestParam(value = "cust_id", required = false) String customerId,
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "5") int size) {

    var transactions =
      this.transactionHistoryService.queryTransactions(id, accountNumber, customerId, page, size);
    return ResponseEntity.ok(transactions);
  }
}
