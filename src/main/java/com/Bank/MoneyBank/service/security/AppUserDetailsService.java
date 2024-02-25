package com.Bank.MoneyBank.service.security;

import com.Bank.MoneyBank.config.InternetBankingCustomerDetails;
import com.Bank.MoneyBank.config.OfficerDetails;
import com.Bank.MoneyBank.models.InternetBankingCustomer;
import com.Bank.MoneyBank.models.Officer;
import com.Bank.MoneyBank.repository.InternetBankingCustomersRepo;
import com.Bank.MoneyBank.repository.OfficerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private OfficerRepo officerRepo;

    @Autowired
    private InternetBankingCustomersRepo internetBankingCustomersRepo;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Officer> officer = officerRepo.findByUserName(username);
        Optional<InternetBankingCustomer> iCustomer = internetBankingCustomersRepo.findByUserName(username);
        if (officer.isPresent()) {
            if (officer.get().getRole().equals("OFFICER")) {
                return new OfficerDetails(officer.get());
            }
        } else if (iCustomer.isPresent()) {
            if (iCustomer.get().getRole().equals("CUSTOMER")) {
                return new InternetBankingCustomerDetails(iCustomer.get());
            }
        }
        throw new UsernameNotFoundException("User not found");
    }
}