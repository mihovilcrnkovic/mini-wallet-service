package com.example.demo.service;

import com.example.demo.exception.WalletNotCreatedException;
import com.example.demo.mapper.WalletMapper;
import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import com.example.demo.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletMapper walletMapper;

    @Mock
    TransactionService transactionService;

    @InjectMocks
    WalletService walletService;

    @Test
    public void should_CreateWallet() {
        WalletDto walletDto = new WalletDto(null, "newuser", null);
        WalletDto expected = new WalletDto(null, "newuser", walletService.STARTING_BALANCE);
        Wallet wallet = new Wallet(null, "newuser", walletService.STARTING_BALANCE);

        when(walletMapper.toModel(walletDto)).thenReturn(wallet);
        when(walletMapper.toDto(eq(wallet))).thenReturn(expected);
        when(walletRepository.save(eq(wallet))).thenReturn(wallet);

        WalletDto result = walletService.createWallet(walletDto);
        assertEquals(expected, result);
    }

    @Test
    public void should_NotCreateWallet_when_UsernameIsEmpty() {
        WalletDto walletDto = new WalletDto(null, "", null);
        WalletNotCreatedException e = assertThrows(WalletNotCreatedException.class, () -> walletService.createWallet(walletDto));
        assertEquals("Username is empty.", e.getMessage());
    }

    @Test
    public void should_NotCreateWallet_when_UsernameIsNull() {
        WalletDto walletDto = new WalletDto(null, null, null);
        WalletNotCreatedException e = assertThrows(WalletNotCreatedException.class, () -> walletService.createWallet(walletDto));
        assertEquals("Username is null.", e.getMessage());
    }

    @Test
    public void should_NotCreateWallet_when_UsernameAlreadyExists() {
        WalletDto walletDto = new WalletDto(null, "test", null);
        Wallet wallet = new Wallet(1l, "test", BigDecimal.valueOf(200l));

        when(walletRepository.findWalletByUsername(eq(wallet.getUsername()))).thenReturn(Optional.of(wallet));

        WalletNotCreatedException e = assertThrows(WalletNotCreatedException.class, () -> walletService.createWallet(walletDto));
        assertEquals("Wallet with username = test already exists.", e.getMessage());
    }
}
