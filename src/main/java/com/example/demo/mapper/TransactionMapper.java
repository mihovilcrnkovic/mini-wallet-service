package com.example.demo.mapper;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "senderWalletId", target = "senderWallet.id")
    @Mapping(source = "receiverWalletId", target = "receiverWallet.id")
    Transaction toModel(TransactionDto transactionDto);

    @Mapping(source = "senderWallet.id", target = "senderWalletId")
    @Mapping(source = "receiverWallet.id", target = "receiverWalletId")
    TransactionDto toDto(Transaction transaction);
}
