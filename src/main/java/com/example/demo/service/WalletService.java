package com.example.demo.service;

import com.example.demo.exception.WalletNotCreatedException;
import com.example.demo.exception.WalletNotFoundException;
import com.example.demo.mapper.WalletMapper;
import com.example.demo.model.TransactionDto;
import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import com.example.demo.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WalletService {

    private WalletRepository walletRepository;
    private WalletMapper walletMapper;
    private TransactionService transactionService;
    public static final BigDecimal STARTING_BALANCE = BigDecimal.valueOf(100l);

    @Transactional
    public WalletDto createWallet(WalletDto walletDto){
        checkWalletUsername(walletDto.getUsername());
        Wallet wallet = walletMapper.toModel(walletDto);
        wallet.setBalance(STARTING_BALANCE);

        wallet = walletRepository.save(wallet);
        transactionService.createInitialTransaction(wallet);

        WalletDto result = walletMapper.toDto(wallet);
        return result;
    }

    private void checkWalletUsername(String username) {
        if (username == null) {
            throw new WalletNotCreatedException("Username is null.");
        } else if (username.equals("")) {
            throw new WalletNotCreatedException("Username is empty.");
        } else {
            Optional<Wallet> optionalWallet = walletRepository.findWalletByUsername(username);
            if (optionalWallet.isPresent()) {
                throw new WalletNotCreatedException("Wallet with username = " + username + " already exists.");
            }
        }
    }

    public WalletDto getWalletById(Long id) {
        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if (optionalWallet.isEmpty()) {
            throw new WalletNotFoundException("Wallet with id = " + id + " not found.");
        }

        Wallet wallet = optionalWallet.get();
        WalletDto walletDto = walletMapper.toDto(wallet);

        return walletDto;
    }
}
