package com.example.demo.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WalletDto walletDto)) return false;
        return Objects.equals(id, walletDto.id) && Objects.equals(username, walletDto.username) && Objects.equals(balance, walletDto.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, balance);
    }
}
