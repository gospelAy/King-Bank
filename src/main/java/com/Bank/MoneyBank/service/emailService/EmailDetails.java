package com.Bank.MoneyBank.service.emailService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String subject;
    private String recipientMailAddress;
    private String message;
    private String attachment;
}