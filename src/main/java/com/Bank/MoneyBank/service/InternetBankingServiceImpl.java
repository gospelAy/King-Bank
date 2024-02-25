package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.dtos.request.*;
import com.Bank.MoneyBank.dtos.response.*;
import com.Bank.MoneyBank.exceptions.CardException;
import com.Bank.MoneyBank.exceptions.CustomerNotFound;
import com.Bank.MoneyBank.exceptions.RestrictedAccountException;
import com.Bank.MoneyBank.models.*;
import com.Bank.MoneyBank.repository.InternetBankingCustomersRepo;
import com.Bank.MoneyBank.service.emailService.EmailDetails;
import com.Bank.MoneyBank.service.emailService.EmailSenderService;
import com.Bank.MoneyBank.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import static com.Bank.MoneyBank.models.AccountStatus.RESTRICTED;
import static com.Bank.MoneyBank.models.CardStatus.DEACTIVATED;
import static com.Bank.MoneyBank.models.CardStatus.EXPIRED;
import static com.Bank.MoneyBank.models.TransactionStatus.SUCCESS;
import static com.Bank.MoneyBank.models.TransactionType.E_CREDIT;
import static com.Bank.MoneyBank.models.TransactionType.E_DEBIT;
import static com.Bank.MoneyBank.utils.AccountUtils.*;

@Service
@RequiredArgsConstructor
public class InternetBankingServiceImpl implements InternetBankingService {

    private final InternetBankingCustomersRepo internetBankingCustomersRepo;
    private final TransactionService transactionService;
    private final EmailSenderService emailSenderService;
    private final CardService cardService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse authenticateAndGetToken(AuthRequest authRequest) {
        try {
            String token;
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
                            authRequest.getPassword()));
            if (authentication.isAuthenticated()) token = jwtService.generateToken(authRequest.getUserName());
            else throw new IllegalArgumentException("User not found");
            InternetBankingCustomer customer = internetBankingCustomersRepo.findByUserName(authRequest.getUserName()).get();
            return AuthResponse.builder()
                    .userId(customer.getId())
                    .message("Authentication successful")
                    .token(token)
                    .build();
        } catch (Exception ex) {
            return AuthResponse.builder()
                    .message(ex.getMessage())
                    .build();
        }
    }

    @Override
    public InternetBankingRegistrationResponse signUp(RegisterForInternetBanking request) {
        try {
            Optional<InternetBankingCustomer> customer1 = internetBankingCustomersRepo.findByUserName(request.getUserName());
            if (customer1.isPresent()) throw new RuntimeException("User name is taken. User another one");
            InternetBankingCustomer internetBankingCustomer = internetBankingCustomersRepo.findByAccountNumber(request.getAccountNumber());
            if (internetBankingCustomer != null) throw new RuntimeException("Have you signed up recently?");
            Customer customer = userService.findByAccountNumber(request.getAccountNumber());
            if (customer == null) throw new RuntimeException("Provide correct acc details");
            Card card = cardService.findCardByCustomerId(customer.getId());
            checkIfCardIsNull(card);
            checkCardStatus(card);
            if (!compareDetails(request.getCardNumber(), card.getCardNumber()))
                throw new CardException("Provide correct card details");
            if (!compareDetails(request.getCardPin(), card.getPin()))
                throw new CardException("Provide correct card details");
            InternetBankingCustomer iCustomer = getiCustomer(request, customer);
            InternetBankingCustomer iCustomer2 = internetBankingCustomersRepo.save(iCustomer);
            return InternetBankingRegistrationResponse.builder().id(iCustomer2.getId()).message("Sign up successful").build();
        } catch (Exception ex) {
            return InternetBankingRegistrationResponse.builder().message(ex.getMessage()).build();
        }
    }

    private void checkCardStatus(Card card) {
        if (card.getStatus() == EXPIRED || card.getStatus() == DEACTIVATED) throw new CardException("Invalid card");
    }


    private void checkIfCardIsNull(Card card) {
        if (card == null) throw new CardException("Provide correct card details");
    }

    @Override
    public CardResponse deActivateCard(CardDeactivationRequest request) {
        return cardService.deActivateCard(request);
    }

    @Override
    public Response transfer(TransferRequest request) {
        boolean isValidDestination = userService.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isValidDestination) return Response.builder().code(ACCOUNT_NOT_FOUND_CODE)
                .message(INVALID_DESTINATION_MESSAGE).build();
        InternetBankingCustomer internetBankingCustomer = internetBankingCustomersRepo.findByAccountNumber(request.getSourceAccountNumber());
        Customer sourceAccount = userService.findByAccountNumber(request.getSourceAccountNumber());
        checkIfSourceAccountIsSignedUpForInternetBanking(internetBankingCustomer, sourceAccount);
        checkAccountStatus(sourceAccount, "PERMISSION DENIED. SOURCE ACCOUNT WAS RESTRICTED");
        if (request.getAmount().compareTo(sourceAccount.getAccountBalance()) > 0)
            return Response.builder().code(ACCOUNT_DEBIT_DECLINED_CODE).message(ACCOUNT_DEBIT_DECLINED_MESSAGE).build();
        BigDecimal debitedAmount = request.getAmount();
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(debitedAmount));
        Customer destinationAccount = userService.findByAccountNumber(request.getDestinationAccountNumber());
        checkAccountStatus(destinationAccount, "PERMISSION DENIED. DESTINATION ACCOUNT WAS RESTRICTED");
        BigDecimal updatedDestinationBalance = destinationAccount.getAccountBalance().add(debitedAmount);
        destinationAccount.setAccountBalance(updatedDestinationBalance);

        userService.save(destinationAccount);
        userService.save(sourceAccount);
        Transaction sourceTransaction = Transaction.builder().type(E_DEBIT).amount(request.getAmount()).accountNumber(destinationAccount.getAccountNumber())
                .customer(sourceAccount).status(SUCCESS).date(LocalDate.now()).time(LocalTime.now()).build();
        transactionService.save(sourceTransaction);
        sourceAccount.getTransactionList().add(sourceTransaction);
        String debitMessage = "\nDear " + sourceAccount.getFirstName() + "\nA debit transaction occurred on your account." +
                "\nAmount: " + request.getAmount() + "\nCurrent balance: " + sourceAccount.getAccountBalance() +
                "\nDestination: " + destinationAccount.getFirstName() + " " + destinationAccount.getLastName() + "\nDate: " + LocalDate.now() + "\nTime: " + LocalTime.now();
        Transaction destinationTransaction = Transaction.builder().type(E_CREDIT).amount(request.getAmount()).accountNumber(sourceAccount.getAccountNumber())
                .customer(destinationAccount).status(SUCCESS).date(LocalDate.now()).time(LocalTime.now())
                .build();
        transactionService.save(destinationTransaction);
        destinationAccount.getTransactionList().add(destinationTransaction);
        EmailDetails debitDetails = mailMessage(sourceAccount, "Debit transaction notification", sourceAccount.getEmail(), debitMessage);
        emailSenderService.sendMail(debitDetails);
        String creditMessage = "\nDear " + destinationAccount.getFirstName() + "\nA credit transaction occurred on your account." +
                "\nAmount: " + request.getAmount() + "\nCurrent balance: " + destinationAccount.getAccountBalance() +
                "\nFrom: " + sourceAccount.getFirstName() + " " + sourceAccount.getLastName() + "\nDate: " + LocalDate.now() + "\nTime: " + LocalTime.now();

        EmailDetails creditDetails = mailMessage(sourceAccount, "Credit transaction notification", destinationAccount.getEmail(), creditMessage);
        emailSenderService.sendMail(creditDetails);
        return Response.builder().code(TRANSFER_SUCCESS_CODE).message(TRANSFER_SUCCESS_MESSAGE).build();
    }

    private void checkAccountStatus(Customer account, String message) {
        if (account.getAccountStatus().equals(RESTRICTED))
            throw new RestrictedAccountException(message);
    }

    private void checkIfSourceAccountIsSignedUpForInternetBanking(InternetBankingCustomer internetBankingCustomer, Customer sourceAccount) {
        if (!Objects.equals(internetBankingCustomer.getAccountNumber(), sourceAccount.getAccountNumber()))
            throw new CustomerNotFound("PERMISSION DENIED. THIS ACCOUNT IS NOT SIGNED UP FOR INTERNET BANKING");
    }

    @Override
    public CardResponse changeCardPin(ChangeCardPinRequest request) {
        return cardService.changeCardPin(request);
    }

    @Override
    public TransactionHistoryResponse getCustomerTransactions(TransactionHistoryRequest request) {
        try {
            Customer customer = userService.findByAccountNumber(request.getAccountNumber());
            if (customer == null) throw new CustomerNotFound(ACCOUNT_NOT_FOUND_MESSAGE);
            return TransactionHistoryResponse.builder().code(ACCOUNT_FOUND_CODE).message(ACCOUNT_FOUND_MESSAGE).transactionList(customer.getTransactionList()).build();
        } catch (Exception ex) {
            return TransactionHistoryResponse.builder().code(ACCOUNT_NOT_FOUND_CODE).message(ex.getMessage()).transactionList(null).build();
        }
    }

    @Override
    public Response checkAccountBalance(EnquiryRequest enquiryRequest) {
        if (!isValidAccountNumber(enquiryRequest))
            return Response.builder().code(ACCOUNT_NOT_FOUND_CODE).message(ACCOUNT_NOT_FOUND_MESSAGE).build();
        Customer user = userService.findByAccountNumber(enquiryRequest.getAccountNumber());
        return Response.builder().code(ACCOUNT_FOUND_CODE).message(ACCOUNT_FOUND_MESSAGE).accountDetails(getAccountDetails(user)).build();
    }

    private boolean isValidAccountNumber(EnquiryRequest enquiryRequest) {
        return userService.existsByAccountNumber(enquiryRequest.getAccountNumber());
    }


    private AccountDetails getAccountDetails(Customer savedUser) {
        return AccountDetails.builder().accountName(savedUser.getFirstName() + " " +
                        savedUser.getLastName() + " " +
                        savedUser.getMiddleName())
                .accountBalance(savedUser.getAccountBalance())
                .accountNumber(savedUser.getAccountNumber())
                .build();
    }


    private EmailDetails mailMessage(Customer savedUser, String subject, String email, String message) {
        return EmailDetails.builder()
                .subject(subject)
                .recipientMailAddress(email)
                .message(message)
                .build();
    }

    private InternetBankingCustomer getiCustomer(RegisterForInternetBanking request, Customer customer) {
        return InternetBankingCustomer.builder()
                .accountNumber(customer.getAccountNumber()).password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName()).transactionPin(hashDetails(request.getPreferredTransactionPin()))
                .role(String.valueOf(Role.CUSTOMER))
                .build();
    }

    private static String hashDetails(String detail) {
        return BCrypt.hashpw(detail, BCrypt.gensalt());
    }

    private static boolean compareDetails(String candidate, String detail) {
        return BCrypt.checkpw(candidate, detail);
    }
}
