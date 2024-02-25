package com.Bank.MoneyBank.repository;

import com.Bank.MoneyBank.models.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficerRepo extends JpaRepository<Officer, Long> {
    Optional<Officer> findByUserName(String userName);
}
