package com.Bank.MoneyBank.controller;

import com.Bank.MoneyBank.dtos.request.*;
import com.Bank.MoneyBank.service.OfficerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/officer/")
@Tag(name = "Officer Account API's")
@CrossOrigin(origins = "*")
public class OfficerController {
    private final OfficerService officerService;

    @Operation(
            summary = "Authenticates a bank officer",
            description = "Given the required details, a jwt token is generated"
    )
    @PostMapping("login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest request) {
        return new ResponseEntity<>(officerService.authenticateAndGetToken(request), HttpStatus.OK);
    }

    @Operation(
            summary = "creates a new customer account",
            description = "creates a new customer, store customer in the database, gives an Id to the user" +
                    "and customer receives a mail alert"
    )

    @PostMapping("new_account")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(officerService.createBankAccount(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Checks account balance for a customer",
            description = "Given that a customer requires for it and provides acc number, it fetches account balance from the database"
    )

    @GetMapping("account_balance")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> checkAccountBalance(@RequestBody EnquiryRequest request) {
        return new ResponseEntity<>(officerService.checkAccountBalance(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Gets the account name for a customer account",
            description = "Given that a customer requires for it and provides acc number, it fetches customer name from the database"
    )
    @GetMapping("account_name")

    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> checkAccountName(@RequestBody EnquiryRequest request) {
        return new ResponseEntity<>(officerService.checkAccountName(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Credits a customer account",
            description = "Adds money to a customer's account, updates it in the database and sends an alert to the credited customer's mail"
    )
    @PostMapping("credit")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> creditAccount(@RequestBody CreditDebitRequest request) {
        return new ResponseEntity<>(officerService.creditAccount(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Debits a customer account",
            description = "Removes money from a customer's account, updates it in the database and sends an alert to the debited customer's mail"
    )
    @PostMapping("debit")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> debitAccount(@RequestBody CreditDebitRequest request) {
        return new ResponseEntity<>(officerService.debitAccount(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Gets transaction history of a customer",
            description = "Gets a customer's transactions (banking hall credit transactions) through their acc number"
    )
    @GetMapping("transaction_history")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> getCustomerTransactions(@RequestBody TransactionHistoryRequest request) {
        return new ResponseEntity<>(officerService.getAllTransactionsDoneByCustomer(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Restricts a customer's account",
            description = "Restricted account wil not be able to receive or send money. Customer will also be alerted through their email address"
    )
    @PostMapping("account_restriction")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> restrictBankAccount(@RequestBody RestrictAccountRequest request) {
        return new ResponseEntity<>(officerService.restrictBankAccount(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Reactivates a customer's account",
            description = "Reactivates a customer's account. Customers will be informed through their mail"
    )
    @PostMapping("account_reactivation")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> activateBankAccount(@RequestBody ActivateAccount request) {
        return new ResponseEntity<>(officerService.activateBankAccount(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Creates a card for a customer",
            description = "Creates a card for a customer and deducts service fee of 1000 fom the customer's account, " +
                    "Customer will also receive a mail alert. This card is unactivated and the pin provided cannot be used for transactions until a bank officer activates it"
    )
    @PostMapping("card_creation")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> createCard(@RequestBody RequestForCard request) {
        return new ResponseEntity<>(officerService.createCard(request), HttpStatus.OK);
    }


    @Operation(
            summary = "Deactivates a customer's card",
            description = "Deactivates a customer's card. Card cannot be used for any transaction. " +
                    "Customers also receive an alert through their mail"
    )
    @PostMapping("card_deactivation")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> deactivateCard(@RequestBody DeactivateCard request) {
        return new ResponseEntity<>(officerService.deActivateCard(request), HttpStatus.OK);
    }


    @Operation(
            summary = " Reactivates a customer's card",
            description = "Reactivates a customer's card. Card can now be used for any transaction. " +
                    "The customer also receive an alert through their mail"
    )
    @PostMapping("card_reactivation")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> reactivateCard(@RequestBody DeactivateCard request) {
        return new ResponseEntity<>(officerService.reActivateCard(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Activates a new card",
            description = "Customers are required to replace the default pin that comes with a new card"
    )
    @PostMapping("card_activation")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> activateCard(@RequestBody ChangeCardPinRequest request) {
        return new ResponseEntity<>(officerService.activateCard(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieves all transactions done by an officer"
    )
    @GetMapping("transactions/{id}")
    @PreAuthorize("hasAuthority('OFFICER')")
    public ResponseEntity<?> activateCard(@PathVariable Long id) {
        return new ResponseEntity<>(officerService.retrieveOfficerTransactions(id), HttpStatus.OK);
    }
}

