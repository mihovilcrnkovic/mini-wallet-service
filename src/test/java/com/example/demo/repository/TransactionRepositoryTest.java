package com.example.demo.repository;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.model.Transaction;
import com.example.demo.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    void should_FindTransactionByReceiverId() {
        Wallet sender = new Wallet(1l, "mcrnkovic", BigDecimal.valueOf(150l));
        Wallet receiver = new Wallet(2l, "testuser", BigDecimal.valueOf(200l));;
        Transaction transaction = new Transaction(null, sender, receiver, BigDecimal.valueOf(50l), LocalDateTime.now(), null);
        transaction = transactionRepository.save(transaction);

        List<Transaction> results = transactionRepository.findAllByReceiverWalletId(receiver.getId());
        Transaction finalTransaction = transaction;
        Optional<Transaction> optionalResult = results.stream().filter(result -> result.getId().equals(finalTransaction.getId()))
                .findFirst();

        assertTrue(optionalResult.isPresent());
    }
}
