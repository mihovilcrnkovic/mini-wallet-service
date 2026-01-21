package com.example.demo.mapper;

import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalletMapperTest {

    WalletMapper walletMapper = Mappers.getMapper(WalletMapper.class);

    @Test
    void toDtoTest() {
        Wallet wallet = new Wallet(1l, "john_doe", BigDecimal.valueOf(50l));
        WalletDto walletDto = walletMapper.toDto(wallet);

        checkEquals(wallet, walletDto);
    }

    @Test
    void toModelTest() {
        WalletDto walletDto = new WalletDto(1l, "john_doe", BigDecimal.valueOf(50l));
        Wallet wallet = walletMapper.toModel(walletDto);

        checkEquals(wallet, walletDto);
    }

    private static void checkEquals(Wallet wallet, WalletDto walletDto) {
        assertEquals(wallet.getId(), walletDto.getId());
        assertEquals(wallet.getUsername(), walletDto.getUsername());
        assertEquals(wallet.getBalance(), walletDto.getBalance());
    }
}
