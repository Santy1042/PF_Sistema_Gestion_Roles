package com.thegroup.pf_sgr.interfaces;

import com.thegroup.pf_sgr.dto.CheckoutResponse;
import com.thegroup.pf_sgr.dto.SaleResponse;
import com.thegroup.pf_sgr.dto.AdminSaleUpdateRequest;
import java.util.List;

public interface ISaleService {
    CheckoutResponse createOrder(Long userId);
    SaleResponse confirmPayment(Long saleId, Long userId, String statusReport);
    SaleResponse cancelOrder(Long saleId, Long userId, String statusReport);
    SaleResponse refundOrder(Long saleId);
    List<SaleResponse> getSalesByUser(Long userId);
    SaleResponse getSaleDetail(Long saleId, Long userId);
    List<SaleResponse> getAllSales();
    SaleResponse updateSaleAdmin(Long saleId, AdminSaleUpdateRequest request);
}
