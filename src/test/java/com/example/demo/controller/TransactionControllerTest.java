package com.example.demo.controller;


import com.example.demo.AbstractIntegrationTest;
import com.example.demo.model.TransactionDto;
import com.example.demo.model.Wallet;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.WalletRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class TransactionControllerTest extends AbstractIntegrationTest {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    JsonMapper jsonMapper;

    Wallet sender = new Wallet(1l, "mcrnkovic", BigDecimal.valueOf(100l));
    Wallet receiver = new Wallet(2l, "testuser", BigDecimal.valueOf(150l));

    @AfterEach
    void resetWallets() {
        walletRepository.save(sender);
        walletRepository.save(receiver);
        transactionRepository.deleteAll();
    }

    @Test
    void should_TransferAmount() throws Exception {

        Optional<Wallet> optionalSender = walletRepository.findById(1l);
        Optional<Wallet> optionalReceiver = walletRepository.findById(2l);
        Wallet senderAtStart = optionalSender.get();
        Wallet receiverAtStart = optionalReceiver.get();

        MockHttpServletRequestBuilder request = post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderWalletId\": \"1\",\"receiverWalletId\": \"2\",\"amount\": 50.00}");

        MvcResult result = mockMvc.perform(request)
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.senderWalletId").value(1))
                .andExpect(jsonPath("$.receiverWalletId").value(2))
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(status().isOk())
                .andReturn();
        TransactionDto transactionDto = jsonMapper.readValue(result.getResponse().getContentAsString(), TransactionDto.class);

        optionalSender = walletRepository.findById(1l);
        optionalReceiver = walletRepository.findById(2l);
        Wallet senderAtEnd = optionalSender.get();
        Wallet receiverAtEnd = optionalReceiver.get();

        assertTrue(senderAtStart.getBalance().subtract(transactionDto.getAmount()).equals(senderAtEnd.getBalance()));
        assertTrue(receiverAtStart.getBalance().add(transactionDto.getAmount()).equals(receiverAtEnd.getBalance()));
    }

    @Test
    void should_ReturnBadRequest_when_TransferFails() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderWalletId\": \"1\",\"receiverWalletId\": \"2\",\"amount\": -50.00}");

        mockMvc.perform(request)
                .andExpect(content().string("Invalid transaction amount."))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

}
