package com.Bank.MoneyBank.controller;

import com.Bank.MoneyBank.dtos.request.*;
import com.Bank.MoneyBank.service.InternetBankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/iCustomer/")
@Tag(name = "Internet Banking API's")
@CrossOrigin(origins = "*")
public class InternetBankingController {
    private final InternetBankingService internetBankingService;


    @Operation(
            summary = "Adds a customer to the internet banking platform",
            description = "Given the required details, adds up a customer to the internet banking platform"
    )

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody RegisterForInternetBanking registerForInternetBanking){
        return new ResponseEntity<>(internetBankingService.signUp(registerForInternetBanking), HttpStatus.OK);
    }

    @Operation(
            summary = "Authenticates a bank officer",
            description = "Given the required details, a jwt token is generated"
    )
    @PostMapping("login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest request) {
        return new ResponseEntity<>(internetBankingService.authenticateAndGetToken(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Change card pin",
            description = "Given the required details, change card pin"
    )
    @PostMapping("pin")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> changeCardPin(@RequestBody ChangeCardPinRequest changeCardPinRequest){
        return new ResponseEntity<>(internetBankingService.changeCardPin(changeCardPinRequest), HttpStatus.OK);
    }
    @Operation(
            summary = "Check account balance",
            description = "Given the required details, gets account balance for the user"
    )
    @GetMapping("account_balance")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> checkAccountBalance(@RequestBody EnquiryRequest request){
        return new ResponseEntity<>(internetBankingService.checkAccountBalance(request), HttpStatus.OK);
    }
    @Operation(
            summary = "Transfer from one account to another",
            description = "Given the required details, transfer to another account. Each account gets a mail notification"
    )

    @PostMapping("transfer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request){
        return new ResponseEntity<>(internetBankingService.transfer(request), HttpStatus.OK);
    }
    @Operation(
            summary = "Gets transaction history of a customer",
            description = "Given the required details, gets transaction history from the database"
    )

    @GetMapping("transaction_history")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getCustomerTransactions(@RequestBody TransactionHistoryRequest request){
        return new ResponseEntity<>(internetBankingService.getCustomerTransactions(request), HttpStatus.OK);
    }
    @Operation(
            summary = "Deactivates a customer's card",
            description = "Deactivates a customer's card. Card cannot be used for any transaction. " +
                    "The customer also receive an alert through their mail"
    )

    @PostMapping("card_deactivation")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> deactivateCard(@RequestBody CardDeactivationRequest request){
        return new ResponseEntity<>(internetBankingService.deActivateCard(request), HttpStatus.OK);
    }
}

