package com.Bank.MoneyBank.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
}
