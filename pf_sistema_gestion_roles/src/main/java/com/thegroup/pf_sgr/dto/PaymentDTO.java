package com.thegroup.pf_sgr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long idPayment;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
}
