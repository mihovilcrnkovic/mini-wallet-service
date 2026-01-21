package com.example.demo.mapper;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDto;
import com.example.demo.model.Wallet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {

    TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void toDtoTest() {
        Wallet sender = new Wallet(1l, "sender", BigDecimal.valueOf(200l));
        Wallet receiver = new Wallet(2l, "receiver", BigDecimal.valueOf(100l));
        Transaction transaction = new Transaction(1l, sender, receiver, BigDecimal.valueOf(50l), LocalDateTime.now(), "Food & drinks");
        TransactionDto transactionDto = transactionMapper.toDto(transaction);

        checkEquals(transaction, transactionDto);
    }

    @Test
    void toModelTest() {
        TransactionDto transactionDto = new TransactionDto(1l, 1l, 2l, BigDecimal.valueOf(50));
        Transaction transaction = transactionMapper.toModel(transactionDto);

        checkEquals(transaction, transactionDto);
    }

    private static void checkEquals(Transaction transaction, TransactionDto transactionDto) {
        assertEquals(transaction.getId(), transactionDto.getId());
        assertEquals(transaction.getAmount(), transactionDto.getAmount());
        assertEquals(transaction.getSenderWallet().getId(), transactionDto.getSenderWalletId());
        assertEquals(transaction.getReceiverWallet().getId(), transactionDto.getReceiverWalletId());
    }
}
