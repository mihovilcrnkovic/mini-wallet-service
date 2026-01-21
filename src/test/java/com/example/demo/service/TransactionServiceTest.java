package com.example.demo.service;

import com.example.demo.exception.TransferFailedException;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDto;
import com.example.demo.model.Wallet;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionServiceTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransactionService transactionService;

    static Wallet sender;
    static Wallet receiver;
    static Optional<Wallet> optionalSender;
    static Optional<Wallet> optionalReceiver;

    @BeforeEach
    void mockSetup() {
        sender = new Wallet(1l, "sender", BigDecimal.valueOf(100l));
        receiver = new Wallet(2l, "receiver", BigDecimal.valueOf(150l));
        optionalSender = Optional.of(sender);
        optionalReceiver = Optional.of(receiver);
        when(walletRepository.findById(1l)).thenReturn(optionalSender);
        when(walletRepository.findById(2l)).thenReturn(optionalReceiver);
    }

    private void stubMapperMethods(Transaction transaction, TransactionDto transactionDto){
        when(transactionMapper.toDto(eq(transaction))).thenReturn(transactionDto);
        when(transactionMapper.toModel(eq(transactionDto))).thenReturn(transaction);
    }

    private Transaction createTransactionFromDto(TransactionDto transactionDto) {
        Wallet senderWallet = new Wallet(transactionDto.getSenderWalletId(), null, null);
        Wallet receiverWallet = new Wallet(transactionDto.getReceiverWalletId(), null, null);
        Transaction transaction = new Transaction(null, senderWallet, receiverWallet,
                transactionDto.getAmount(), null, null);

        return transaction;
    }


    @Test
    void should_DoTransfer() {
        TransactionDto transactionDto = new TransactionDto(null, 1l, 2l, BigDecimal.valueOf(50l));
        Transaction transaction = createTransactionFromDto(transactionDto);

        stubMapperMethods(transaction, transactionDto);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        TransactionDto result = transactionService.transfer(transactionDto);
        assertEquals(transactionDto, result);
    }

    @Test
    void should_ThrowTransferFailedException_when_TransactionAmountNegative() {
        TransactionDto transactionDto = new TransactionDto(null, 1l, 2l, BigDecimal.valueOf(-50l));
        Transaction transaction = createTransactionFromDto(transactionDto);
        stubMapperMethods(transaction, transactionDto);

        TransferFailedException e = assertThrows(TransferFailedException.class, () -> transactionService.transfer(transactionDto));
        assertEquals("Invalid transaction amount.", e.getMessage());
    }

    @Test
    void should_ThrowTransferFailedException_when_InsufficientFunds() {
        TransactionDto transactionDto = new TransactionDto(null, 1l, 2l, BigDecimal.valueOf(150l));
        Transaction transaction = createTransactionFromDto(transactionDto);
        stubMapperMethods(transaction, transactionDto);

        TransferFailedException e = assertThrows(TransferFailedException.class, () -> transactionService.transfer(transactionDto));
        assertEquals("Invalid transaction amount.", e.getMessage());
    }

    @Test
    void should_ThrowTransferFailedException_when_SenderNotExists() {
        optionalSender = Optional.empty();
        when(walletRepository.findById(1l)).thenReturn(optionalSender);

        TransactionDto transactionDto = new TransactionDto(null, 1l, 2l, BigDecimal.valueOf(50l));
        Transaction transaction = createTransactionFromDto(transactionDto);
        stubMapperMethods(transaction, transactionDto);

        TransferFailedException e = assertThrows(TransferFailedException.class, () -> transactionService.transfer(transactionDto));
        assertEquals("Sender or receiver does not exist.", e.getMessage());
    }

    @Test
    void should_ThrowTransferFailedException_when_ReceiverNotExists() {
        optionalReceiver = Optional.empty();
        when(walletRepository.findById(1l)).thenReturn(optionalReceiver);

        TransactionDto transactionDto = new TransactionDto(null, 1l, 2l, BigDecimal.valueOf(50l));
        Transaction transaction = createTransactionFromDto(transactionDto);
        stubMapperMethods(transaction, transactionDto);

        TransferFailedException e = assertThrows(TransferFailedException.class, () -> transactionService.transfer(transactionDto));
        assertEquals("Sender or receiver does not exist.", e.getMessage());
    }

    @Test
    void should_ThrowTransferFailedException_when_SenderIsReceiver() {
        TransactionDto transactionDto = new TransactionDto(null, 1l, 1l, BigDecimal.valueOf(50l));
        Transaction transaction = createTransactionFromDto(transactionDto);
        stubMapperMethods(transaction, transactionDto);

        TransferFailedException e = assertThrows(TransferFailedException.class, () -> transactionService.transfer(transactionDto));
        assertEquals("Sender and receiver are the same.", e.getMessage());
    }

}
