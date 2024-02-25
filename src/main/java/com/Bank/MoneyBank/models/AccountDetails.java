package com.Bank.MoneyBank.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
    @Schema(
            name = "Customer account name"
    )
    private String accountName;
    @Schema(
            name = "Customer account number"
    )
    private String accountNumber;
    @Schema(
            name = "Customer account balance "
    )
    private BigDecimal accountBalance;
}