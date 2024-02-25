package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.models.Customer;
import com.Bank.MoneyBank.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final CustomerRepo userRepo;


    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return userRepo.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Customer findByAccountNumber(String accountNumber) {
        return userRepo.findByAccountNumber(accountNumber);
    }

    @Override
    public Customer save(Customer customer) {
        return userRepo.save(customer);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepo.existsByPhoneNumber(phoneNumber);
    }

}