package com.Bank.MoneyBank.service;


import com.Bank.MoneyBank.dtos.request.*;
import com.Bank.MoneyBank.dtos.response.*;
import com.Bank.MoneyBank.models.Transaction;

import java.util.List;

public interface OfficerService {
    AuthResponse authenticateAndGetToken(AuthRequest authRequest);
    Response createBankAccount(CreateAccountRequest request) ;
    Response checkAccountBalance(EnquiryRequest enquiryRequest);
    String checkAccountName(EnquiryRequest request);
    Response creditAccount(CreditDebitRequest request);
    Response debitAccount(CreditDebitRequest request);
    TransactionHistoryResponse getAllTransactionsDoneByCustomer(TransactionHistoryRequest request);
    //    List<BankingHallTransaction> viewAllBankingHallTransactions();
    RestrictAccountResponse restrictBankAccount(RestrictAccountRequest request);
    RestrictAccountResponse activateBankAccount(ActivateAccount request);
    CardResponse activateCard(ChangeCardPinRequest request);
    CardResponse createCard(RequestForCard request);
    CardResponse deActivateCard(DeactivateCard request);
    CardResponse reActivateCard(DeactivateCard request);
    List<Transaction> retrieveOfficerTransactions(Long id);
}
