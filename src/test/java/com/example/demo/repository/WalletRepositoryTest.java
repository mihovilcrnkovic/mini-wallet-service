package com.example.demo.repository;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WalletRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    WalletRepository walletRepository;

    @Test
    void should_FindAllWallets(){
        List<Wallet> results = walletRepository.findAll();
        assertTrue(results.size() == 3); // from V2__20260115_initial_data.sql
    }

    @Test
    void should_ResultIsPresent_when_WalletIsFoundByUsername(){
        Optional<Wallet> result = walletRepository.findWalletByUsername("mcrnkovic"); // from V2__20260115_initial_data.sql
        assertTrue(result.isPresent());
    }

    @Test
    void should_ResultIsEmpty_when_WalletIsNotFoundByUsername(){
        Optional<Wallet> result = walletRepository.findWalletByUsername("test");
        assertTrue(result.isEmpty());
    }
}
