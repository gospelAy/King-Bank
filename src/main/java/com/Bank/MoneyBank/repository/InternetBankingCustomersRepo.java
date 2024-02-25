package com.Bank.MoneyBank.repository;

import com.Bank.MoneyBank.models.InternetBankingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternetBankingCustomersRepo extends JpaRepository<InternetBankingCustomer, Long> {
    InternetBankingCustomer findByAccountNumber(String accountNumber);

    Optional<InternetBankingCustomer> findByUserName(String username);

    boolean existsByAccountNumber(String destinationAccountNumber);
}

