package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.models.Customer;

public interface UserService {
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByEmail(String email);
    Customer findByAccountNumber(String accountNumber);
    Customer save(Customer customer);
    boolean existsByPhoneNumber(String phoneNumber);
}