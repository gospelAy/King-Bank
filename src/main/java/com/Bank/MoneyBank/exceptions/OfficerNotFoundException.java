package com.Bank.MoneyBank.exceptions;

public class OfficerNotFoundException extends RuntimeException{
    public OfficerNotFoundException(String message){
        super(message);
    }
}