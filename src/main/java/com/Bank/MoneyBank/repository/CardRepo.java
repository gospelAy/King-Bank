package com.Bank.MoneyBank.repository;

import com.Bank.MoneyBank.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepo extends JpaRepository<Card, Long> {
    Card findByCardNumber(String cardNumber);
    Card findByAccountNumber(String accountNumber);
    Card findByCustomerId(Long id);
}