package com.Bank.MoneyBank.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForInternetBanking {
    private String accountNumber;
    private String cardNumber;
    private String cardPin;
    private String preferredTransactionPin;
    private String userName;
    private String password;
}