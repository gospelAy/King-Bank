package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.dtos.request.*;
import com.Bank.MoneyBank.dtos.response.*;

public interface InternetBankingService {
    AuthResponse authenticateAndGetToken(AuthRequest authRequest);
    InternetBankingRegistrationResponse signUp(RegisterForInternetBanking request);
    Response transfer(TransferRequest request);
    TransactionHistoryResponse getCustomerTransactions(TransactionHistoryRequest request);
    CardResponse changeCardPin(ChangeCardPinRequest request);
    CardResponse deActivateCard(CardDeactivationRequest request);
    Response checkAccountBalance(EnquiryRequest enquiryRequest);
}
