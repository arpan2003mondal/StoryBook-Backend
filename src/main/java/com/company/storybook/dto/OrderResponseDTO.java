package com.company.storybook.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Integer itemCount;
    private LocalDateTime createdAt;
}
