package com.Bank.MoneyBank.dtos.response;

import com.Bank.MoneyBank.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {
    private String message;
    private String code;
    private List<Transaction> transactionList;
}

