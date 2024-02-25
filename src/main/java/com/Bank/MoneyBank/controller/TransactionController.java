package com.Bank.MoneyBank.controller;

import com.Bank.MoneyBank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction/")
@CrossOrigin(origins = "*")
@Tag(name = "Transaction Api")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Gets details of a particular transaction done by a customer on internet banking",
            description = "Gets details of a particular transaction done by a customer on internet banking. Only" +
                    "an officer can access this service. Hence you need a token that was generated when the officer/login" +
                    "endpoint was called"
    )
    @GetMapping("{transactionId}")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId){
        return new ResponseEntity<>(transactionService.getATransactionDetails(transactionId), HttpStatus.OK);
    }
}