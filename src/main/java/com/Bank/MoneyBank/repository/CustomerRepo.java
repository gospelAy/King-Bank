package com.Bank.MoneyBank.repository;

import com.Bank.MoneyBank.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findById(Long customerId);
    boolean existsByEmail(String email);

    boolean existsByAccountNumber(String accountNumber);

    Customer findByAccountNumber(String accountNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);
}
