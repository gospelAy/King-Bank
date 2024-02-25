package com.Bank.MoneyBank.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternetBankingRegistrationResponse {
    private Long id;
    private String message;
}
