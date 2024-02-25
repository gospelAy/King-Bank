package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.dtos.request.CreditDebitRequest;
import com.Bank.MoneyBank.dtos.request.TransactionHistoryRequest;
import com.Bank.MoneyBank.dtos.response.FoundTransactionResponse;
import com.Bank.MoneyBank.dtos.response.TransactionHistoryResponse;
import com.Bank.MoneyBank.models.*;

public interface TransactionService {
    Transaction save(Transaction transaction);
    //    BankingHallTransaction saveTransaction(BankingHallTransaction bankingHallTransaction);
    FoundTransactionResponse getATransactionDetails(String id);
    //    List<BankingHallTransaction> viewAllBankingHallTransactions();
    TransactionHistoryResponse getAllTransactionsDoneByCustomer(TransactionHistoryRequest request);
    Transaction createTransaction(CreditDebitRequest request, Customer creditedUser,
                                  TransactionType type, TransactionStatus status, Officer officer);
}
