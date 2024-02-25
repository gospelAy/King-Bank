package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.dtos.request.CardDeactivationRequest;
import com.Bank.MoneyBank.dtos.request.ChangeCardPinRequest;
import com.Bank.MoneyBank.dtos.request.DeactivateCard;
import com.Bank.MoneyBank.dtos.request.RequestForCard;
import com.Bank.MoneyBank.dtos.response.CardResponse;
import com.Bank.MoneyBank.exceptions.CardException;
import com.Bank.MoneyBank.exceptions.CustomerNotFound;
import com.Bank.MoneyBank.exceptions.OfficerNotFoundException;
import com.Bank.MoneyBank.models.*;
import com.Bank.MoneyBank.repository.CardRepo;
import com.Bank.MoneyBank.service.emailService.EmailDetails;
import com.Bank.MoneyBank.service.emailService.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import static com.Bank.MoneyBank.models.CardStatus.*;
import static com.Bank.MoneyBank.models.CardType.DEBIT_CARD;
import static com.Bank.MoneyBank.models.TransactionStatus.SUCCESS;
import static com.Bank.MoneyBank.models.TransactionType.DEBIT;
import static com.Bank.MoneyBank.utils.AccountUtils.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{
    private static final String issuerIdentifier = "5045";
    private final CardRepo cardRepo;
    private final UserService userService;
    private final OfficerServiceProxy officerServiceProxy;
    private final EmailSenderService emailSenderService;
    @Override
    public Card findCardByCustomerId(Long id) {
        return cardRepo.findByCustomerId(id);
    }


    @Override
    public CardResponse createCard(RequestForCard request) {
        try {
            Customer customer = userService.findByAccountNumber(request.getAccountNumber());
            if (customer == null) throw new CustomerNotFound(ACCOUNT_NOT_FOUND_MESSAGE);
            if (customer.getAccountBalance().compareTo(new BigDecimal("1000")) < 0)
                throw new CustomerNotFound("Insufficient funds");
            Officer officer = officerServiceProxy.findById(request.getOfficerId())
                    .orElseThrow(() -> new OfficerNotFoundException("OFFICER NOT FOUND"));
            Card foundCard = cardRepo.findByAccountNumber(request.getAccountNumber());
            if (foundCard == null){
                processCard(request, customer, officer);

            }else if (foundCard.getStatus() != DEACTIVATED || foundCard.getStatus() != EXPIRED) {
                throw new RuntimeException("THIS ACCOUNT HAS AN ACTIVE CARD");
            }
            return CardResponse.builder().code(CARD_CREATION_CODE).message(CARD_CREATION_MESSAGE).build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_CREATION_FAILURE).message(ex.getMessage()).build();
        }
    }

    @Override
    public CardResponse deActivateCardByOfficer(DeactivateCard request) {
        try {
            Customer customer = userService.findByAccountNumber(request.getAccountNumber());
            if (customer == null) throw new CustomerNotFound("CUSTOMER NOT FOUND");
            Card card = cardRepo.findByAccountNumber(request.getAccountNumber());
            if (card == null) throw new RuntimeException("Card doesn't exists");
            card.setStatus(DEACTIVATED);
            cardRepo.save(card);
            String message = "\nDear " + card.getCardName() + " \nYour debit card has been restricted." +
                    "\n Please visit any of our branches to rectify this issue" + "\nDate: " + LocalDateTime.now() + "\nTime: " + LocalTime.now();
            EmailDetails details = mailMessage(customer, "Debit card restricted",
                    customer.getEmail(), message);
            emailSenderService.sendMail(details);
            return CardResponse.builder().code(CARD_DEACTIVATION_SUCCESSFUL).message(CARD_DEACTIVATION_SUCCESS_MESSAGE).build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_DEACTIVATION_FAILED).message(ex.getMessage()).build();
        }
    }

    @Override
    public CardResponse activateCard(ChangeCardPinRequest request) {
        try{
            Card foundCard = cardRepo.findByAccountNumber(request.getAccountNumber());
            checkIfCardIsExists(foundCard);
            if(!compareDetails(request.getCardNumber(), foundCard.getCardNumber())) throw new CardException("Invalid details");
            if (!compareDetails(request.getOldPin(), foundCard.getPin())) throw new CardException("Invalid details");
            if (foundCard.getStatus() != UNACTIVATED) throw new CardException("Card is already activated");
            foundCard.setPin(hashDetails(request.getNewPin()));
            foundCard.setStatus(ACTIVATED);
            cardRepo.save(foundCard);
            return CardResponse.builder().code(CARD_ACTIVATION_SUCCESSFUL).message(CARD_ACTIVATION_SUCCESS_MESSAGE).build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_ACTIVATION_FAILED).message(ex.getMessage()).build();
        }
    }

    @Override
    public CardResponse changeCardPin(ChangeCardPinRequest request) {
        try {
            Card card = cardRepo.findByAccountNumber(request.getAccountNumber());
            checkCardStatus(card);
            if(!compareDetails(request.getCardNumber(), card.getCardNumber())) throw new CardException("Invalid details");
            if (!compareDetails(request.getOldPin(), card.getPin())) throw new CardException("Invalid details");
            card.setPin(hashDetails(request.getNewPin()));
            cardRepo.save(card);
            return CardResponse.builder().code(CARD_PIN_CHANGED_CODE).message("Card pin changed successfully").build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_PIN_CHANGED_FAILURE).message(ex.getMessage()).build();
        }
    }
    @Override
    public CardResponse deActivateCard(CardDeactivationRequest request) {
        try {
            Card card = cardRepo.findByAccountNumber(request.getAccountNumber());
            checkIfCardIsExists(card);
            if(!compareDetails(request.getCardNumber(), card.getCardNumber())) throw new CardException("Invalid details");
            if (!compareDetails(request.getCardPin(), card.getPin())) throw new CardException("Invalid details");
            card.setStatus(DEACTIVATED);
            cardRepo.save(card);
            return CardResponse.builder().code(CARD_DEACTIVATION_SUCCESSFUL).message(CARD_DEACTIVATION_SUCCESS_MESSAGE).build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_DEACTIVATION_FAILED).message(ex.getMessage()).build();
        }
    }
    private void checkCardStatus(Card card) {
        if (card.getStatus() == EXPIRED || card.getStatus() == DEACTIVATED) throw new CardException("Invalid card");
    }

    private void checkIfCardIsExists(Card card) {
        if (card == null) throw new CardException("Provide correct card details");
    }
    @Override
    public CardResponse reActivateCard(DeactivateCard request) {
        try {
            Customer customer = userService.findByAccountNumber(request.getAccountNumber());
            if (customer == null)  throw new CustomerNotFound("CUSTOMER NOT FOUND");
            Card card = getByAccountNumber(request);
            if(card == null) throw new CardException("Invalid details");
            card.setStatus(CardStatus.ACTIVATED);
            cardRepo.save(card);
            String message = "\nDear " + card.getCardName() + " \nYour debit card has been activated." +
                    "\n Transactions are now permitted with your card" +
                    "\nDate: " + LocalDateTime.now() + "\nTime: " + LocalTime.now();
            EmailDetails details = mailMessage(customer, "Debit card activated",
                    customer.getEmail(), message);
            emailSenderService.sendMail(details);
            return CardResponse.builder().code(CARD_ACTIVATION_SUCCESSFUL).message(CARD_ACTIVATION_SUCCESS_MESSAGE).build();
        }catch (Exception ex){
            return CardResponse.builder().code(CARD_ACTIVATION_FAILED).message(ex.getMessage()).build();
        }
    }
    private Card getByAccountNumber(DeactivateCard request) {
        return cardRepo.findByAccountNumber(request.getAccountNumber());
    }

    private void processCard(RequestForCard request, Customer customer, Officer officer) {
        String cardNumber = generateCardNumber();
        String cvv = generateCv2();
        String cardPin = generateDefaultCardPin();
        String expiryMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        String expiryYear = String.valueOf(Year.now().plusYears(5).getValue());
        String cardName = customer.getFirstName() + " " + customer.getLastName() + " " + customer.getLastName();
        Card newCard = processCard(customer, officer, cardNumber, cvv, cardPin, expiryMonth, expiryYear, cardName);
        cardRepo.save(newCard);
        BigDecimal amount = customer.getAccountBalance().subtract(new BigDecimal("1000"));
        customer.setAccountBalance(amount);
        Transaction transaction = Transaction.builder().amount(new BigDecimal("1000"))
                .status(SUCCESS).date(LocalDate.now()).time(LocalTime.now()).type(DEBIT).build();
        userService.save(customer);
        customer.getTransactionList().add(transaction);
        String card = "\n\nYou must change card pin for you to be able to use it. Change it on our machines or on internet banking\nCard number: " + cardNumber + "\nCard cvv: " + cvv + "\nCard default pin: " + cardPin
                + "\nCard name: " + cardName + "\nExpiry date :" + expiryMonth + " / " + expiryYear.substring(Math.max(expiryYear.length() - 2, 0)) + "\n";
        String message = "\nDear " + customer.getFirstName() + " \nA debit transaction occurred on your account.\nPurpose: Card issuance fee\n" +
                "\nAmount: " + 1000 + "\nCurrent balance: "  + customer.getAccountBalance() + "\nDate: " + LocalDateTime.now() + "\nTime: " + LocalTime.now();
        EmailDetails details = mailMessage(customer, "Debit transaction notification", customer.getEmail(), message + card);
        emailSenderService.sendMail(details);
    }

    private Card processCard(Customer customer, Officer officer, String cardNumber, String cvv, String cardPin, String expiryMonth, String expiryYear, String cardName) {
        return Card.builder().cardNumber(hashDetails(cardNumber)).customerId(customer.getId())
                .cardType(DEBIT_CARD).cardName(cardName)
                .expiryMonth(expiryMonth).expiryYear(expiryYear)
                .issuingOfficerId(officer.getId()).cv2(hashDetails(cvv)).status(UNACTIVATED)
                .accountNumber(customer.getAccountNumber()).pin(hashDetails(cardPin)).build();
    }

    private EmailDetails mailMessage(Customer savedUser, String subject, String email, String message) {
        return EmailDetails.builder()
                .subject(subject)
                .recipientMailAddress(email)
                .message(message)
                .build();
    }

    private String generateCardNumber() {
        int min = 100_000_000;
        int max = 999_999_999;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        String randomNum = String.valueOf(randomNumber);
        String year = String.valueOf(Year.now().getValue() % 100);
        return issuerIdentifier + year + randomNum.substring(0, 3) + "0" + randomNum.substring(3);
    }

    private String generateCv2() {
        int min = 100;
        int max = 999;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return String.valueOf(randomNumber);
    }

    private String generateDefaultCardPin() {
        int min = 1000;
        int max = 9999;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return String.valueOf(randomNumber);
    }
    private static String hashDetails(String detail){
        return BCrypt.hashpw(detail, BCrypt.gensalt());
    }
    private static boolean compareDetails(String candidate, String detail){
        return BCrypt.checkpw(candidate, detail);
    }
}

