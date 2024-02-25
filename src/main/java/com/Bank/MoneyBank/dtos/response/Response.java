package com.Bank.MoneyBank.dtos.response;

import com.Bank.MoneyBank.models.AccountDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    private String code;
    private String message;
    private AccountDetails accountDetails;
}
