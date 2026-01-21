package com.example.demo.controller;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.model.Transaction;
import com.example.demo.model.Wallet;
import com.example.demo.model.WalletDto;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class WalletControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    JsonMapper jsonMapper;

    @Test
    void should_ReturnWallet() throws Exception {
        Wallet wallet = new Wallet(null, "newWalletUser", BigDecimal.valueOf(100l));
        wallet = walletRepository.save(wallet);

        mockMvc.perform(get("/api/wallets/" + wallet.getId()))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("newWalletUser"))
                .andExpect(jsonPath("$.balance").value(100l))
                .andExpect(status().isOk());
    }

    @Test
    void should_ReturnNotFound_when_WalletNotExists() throws Exception {
        mockMvc.perform(get("/api/wallets/5"))
                .andExpect(content().string("Wallet with id = 5 not found."))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_CreateWallet() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"john_doe\" }");

        MvcResult result = mockMvc.perform(request)
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.balance").value(walletService.STARTING_BALANCE))
                .andExpect(status().isOk())
                .andReturn();

        WalletDto walletDto = jsonMapper.readValue(result.getResponse().getContentAsString(), WalletDto.class);

        Optional<Wallet> optionalWallet = walletRepository.findById(walletDto.getId());
        assertTrue(optionalWallet.isPresent());
    }

    @Test
    void should_CreateInitialTransaction_when_WalletIsCreated() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"john_doe\" }");

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.balance").value(walletService.STARTING_BALANCE))
                .andExpect(status().isOk())
                .andReturn();

        WalletDto walletDto = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), WalletDto.class);

        Optional<Wallet> optionalWallet = walletRepository.findById(walletDto.getId());

        assertTrue(optionalWallet.isPresent());

        if (optionalWallet.isPresent()) {
            Wallet wallet = optionalWallet.get();
            List<Transaction> results = transactionRepository.findAllByReceiverWalletId(wallet.getId());
            Optional<Transaction> optionalTransaction = results.stream().filter(result -> result.getTransactionReason().equals("Initial transaction"))
                    .findFirst();

            assertTrue(optionalTransaction.isPresent());
        }
    }

    @Test
    void should_ReturnBadRequest_when_WalletNotCreated() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"\" }");

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

}
