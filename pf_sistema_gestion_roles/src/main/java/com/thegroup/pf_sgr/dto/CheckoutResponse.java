package com.thegroup.pf_sgr.dto;

import com.thegroup.pf_sgr.model.SaleStatus;
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
public class CheckoutResponse {
    private Long idSale;
    private SaleStatus status;
    private BigDecimal subtotal;
    private BigDecimal total;
    private LocalDateTime saleDate;
}
