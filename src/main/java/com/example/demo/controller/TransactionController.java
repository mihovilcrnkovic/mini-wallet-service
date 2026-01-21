package com.example.demo.controller;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDto;
import com.example.demo.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api")
public class TransactionController {

    TransactionService transactionService;

    @PostMapping("transfers")
    public ResponseEntity transfer(@RequestBody TransactionDto transactionDto) {
        TransactionDto result = transactionService.transfer(transactionDto);
        return new ResponseEntity(result, HttpStatus.OK);
    }



}
