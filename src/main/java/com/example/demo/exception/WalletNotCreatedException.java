package com.example.demo.exception;

public class WalletNotCreatedException extends RuntimeException {
    public WalletNotCreatedException(String message) {
        super(message);
    }
}
