package com.Bank.MoneyBank.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCardPinRequest {
    private String accountNumber;
    private String cardNumber;
    private String oldPin;
    private String newPin;
}
