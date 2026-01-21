package com.example.demo.service;

import com.example.demo.exception.TransferFailedException;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDto;
import com.example.demo.model.Wallet;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;
    private TransactionMapper transactionMapper;

    @Transactional
    public TransactionDto transfer(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toModel(transactionDto);
        Optional<Wallet> optionalSender = walletRepository.findById(transaction.getSenderWallet().getId());
        Optional<Wallet> optionalReceiver = walletRepository.findById(transaction.getReceiverWallet().getId());

        if (optionalSender.isPresent() && optionalReceiver.isPresent()) {
            Wallet sender = optionalSender.get();
            Wallet receiver = optionalReceiver.get();
            if (sender == receiver) {
                throw new TransferFailedException("Sender and receiver are the same.");
            }

            BigDecimal newSenderBalance = sender.getBalance().subtract(transaction.getAmount());
            BigDecimal newReceiverBalance = receiver.getBalance().add(transaction.getAmount());

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0
                && newSenderBalance.compareTo(BigDecimal.ZERO) > -1) {
                sender.setBalance(newSenderBalance);
                receiver.setBalance(newReceiverBalance);

                walletRepository.save(sender);
                walletRepository.save(receiver);

                transaction.setSenderWallet(sender);
                transaction.setReceiverWallet(receiver);
                transaction.setTimestamp(LocalDateTime.now());
                transaction = transactionRepository.save(transaction);
            } else {
                throw new TransferFailedException("Invalid transaction amount.");
            }
        } else {
            throw new TransferFailedException("Sender or receiver does not exist.");
        }

        transactionDto = transactionMapper.toDto(transaction);
        return transactionDto;
    }

    @Transactional
    public TransactionDto createInitialTransaction(Wallet wallet) {
        Transaction transaction = new Transaction();
        transaction.setReceiverWallet(wallet);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAmount(wallet.getBalance());
        transaction.setTransactionReason("Initial transaction");

        transaction = transactionRepository.save(transaction);

        TransactionDto transactionDto = transactionMapper.toDto(transaction);
        return transactionDto;
    }

}
