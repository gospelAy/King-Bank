package com.Bank.MoneyBank.service;

import com.Bank.MoneyBank.models.Officer;
import com.Bank.MoneyBank.repository.OfficerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfficerServiceProxy {
    private final OfficerRepo officerRepo;

    public Optional<Officer> findById(Long id){
        return officerRepo.findById(id);
    }
}
