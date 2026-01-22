package com.example.demo.controller;

import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import com.example.demo.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api")
public class WalletController {

    private WalletService walletService;

    @GetMapping("wallets/{id}")
    public ResponseEntity getWalletById(@PathVariable Long id) {
        WalletDto result = walletService.getWalletById(id);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("wallets")
    public ResponseEntity createWallet(@RequestBody WalletDto wallet) {
        WalletDto result = walletService.createWallet(wallet);
        return new ResponseEntity(result, HttpStatus.OK);
    })
}
