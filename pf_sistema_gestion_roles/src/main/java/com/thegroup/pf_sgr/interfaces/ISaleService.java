package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.CheckoutResponseDTO;
import com.thegroup.pf_sgr.dto.SaleResponseDTO;
import java.util.List;

public interface ISaleService {
    CheckoutResponseDTO createOrder(Long userId);
    SaleResponseDTO confirmPayment(Long saleId, Long userId, String statusReport);
    SaleResponseDTO cancelOrder(Long saleId, Long userId, String statusReport);
    SaleResponseDTO refundOrder(Long saleId);
    List<SaleResponseDTO> getSalesByUser(Long userId);
    SaleResponseDTO getSaleDetail(Long saleId, Long userId);
}
