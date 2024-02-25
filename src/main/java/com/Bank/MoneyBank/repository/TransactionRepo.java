package com.Bank.MoneyBank.repository;

import com.Bank.MoneyBank.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, String> {
}