package com.Bank.MoneyBank.exceptions;

public class RestrictedAccountException extends RuntimeException{
    public RestrictedAccountException(String message){
        super(message);
    }
}
