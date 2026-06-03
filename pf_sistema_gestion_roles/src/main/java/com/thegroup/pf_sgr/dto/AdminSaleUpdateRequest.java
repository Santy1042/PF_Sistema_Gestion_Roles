package com.thegroup.pf_sgr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSaleUpdateRequest {
    private String status;
    private String shippingAddress;
    private String statusReport;
}
