package com.company.storybook.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletBalanceDTO {
    private Long userId;
    private BigDecimal balance;
}
