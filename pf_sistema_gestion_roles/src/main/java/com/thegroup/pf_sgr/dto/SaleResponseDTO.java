package com.thegroup.pf_sgr.dto;

import com.thegroup.pf_sgr.model.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long idSale;
    private Long idUser;
    private SaleStatus status;
    private BigDecimal subtotal;
    private BigDecimal total;
    private LocalDateTime saleDate;
    private String statusReport;
    private String shippingAddress;
    private PaymentDTO payment;
    private List<SaleDetailDTO> details;
}
