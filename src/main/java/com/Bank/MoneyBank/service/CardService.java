package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.dtos.request.CardDeactivationRequest;
import com.Bank.MoneyBank.dtos.request.ChangeCardPinRequest;
import com.Bank.MoneyBank.dtos.request.DeactivateCard;
import com.Bank.MoneyBank.dtos.request.RequestForCard;
import com.Bank.MoneyBank.dtos.response.CardResponse;
import com.Bank.MoneyBank.models.Card;

public interface CardService {
    Card findCardByCustomerId(Long id);
    CardResponse createCard(RequestForCard request);
    CardResponse deActivateCardByOfficer(DeactivateCard request);
    CardResponse activateCard(ChangeCardPinRequest request);
    CardResponse deActivateCard(CardDeactivationRequest request);
    CardResponse reActivateCard(DeactivateCard request);
    CardResponse changeCardPin(ChangeCardPinRequest request);
}
