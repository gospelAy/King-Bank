package com.Bank.MoneyBank.dtos.response;

import com.Bank.MoneyBank.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoundTransactionResponse {
    private String code;
    private String message;
    private Transaction transaction;
}
