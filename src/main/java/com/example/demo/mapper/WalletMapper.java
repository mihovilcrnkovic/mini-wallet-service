package com.example.demo.mapper;

import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toModel(WalletDto walletDto);
    WalletDto toDto(Wallet wallet);
}
