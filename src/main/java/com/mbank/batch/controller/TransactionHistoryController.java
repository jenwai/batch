package com.mbank.batch.controller;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.model.UpdateTrxDescApiRq;
import com.mbank.batch.service.TransactionHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TransactionHistoryController {

  private final TransactionHistoryService transactionHistoryService;

  public TransactionHistoryController(TransactionHistoryService transactionHistoryService) {
    this.transactionHistoryService = transactionHistoryService;
  }

  @GetMapping("/transactions")
  public ResponseEntity<Page<TransactionHistoryDto>> queryTransactions(
    @RequestParam(value = "acc_list", required = false) List<String> accList,
    @RequestParam(value = "cust_id", required = false) String customerId,
    @RequestParam(value = "desc", required = false) String description,
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "5") int size) {

    var transactions =
      this.transactionHistoryService.queryTransactions(accList, customerId, description, page,
        size);
    return ResponseEntity.ok(transactions);
  }

  @PatchMapping("/transactions/{id}/description")
  public ResponseEntity<?> updateTrxDesc(@PathVariable("id") String id,
    @RequestBody UpdateTrxDescApiRq apiRq) {

    try {
      return ResponseEntity.ok(
        this.transactionHistoryService.updateDescription(id, apiRq.getDescription()));
    } catch (ResponseStatusException e) {
      return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }
  }
}
