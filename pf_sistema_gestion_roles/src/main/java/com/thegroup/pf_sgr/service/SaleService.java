package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.dto.CheckoutResponseDTO;
import com.thegroup.pf_sgr.dto.SaleDetailDTO;
import com.thegroup.pf_sgr.dto.SaleResponseDTO;
import com.thegroup.pf_sgr.exception.EmptyCartException;
import com.thegroup.pf_sgr.exception.InsufficientStockException;
import com.thegroup.pf_sgr.exception.InvalidSaleStatusException;
import com.thegroup.pf_sgr.exception.SaleNotFoundException;
import com.thegroup.pf_sgr.interfaces.ISaleService;
import com.thegroup.pf_sgr.model.Cart;
import com.thegroup.pf_sgr.model.CartItem;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.model.Sale;
import com.thegroup.pf_sgr.model.SaleDetail;
import com.thegroup.pf_sgr.model.SaleStatus;
import com.thegroup.pf_sgr.repository.CartItemRepository;
import com.thegroup.pf_sgr.repository.CartRepository;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import com.thegroup.pf_sgr.repository.SaleDetailRepository;
import com.thegroup.pf_sgr.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleService implements ISaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private com.thegroup.pf_sgr.repository.PaymentRepository paymentRepository;

    @Autowired
    private com.thegroup.pf_sgr.repository.PaymentStatusRepository paymentStatusRepository;

    @Autowired
    private com.thegroup.pf_sgr.repository.UserRepository userRepository;

    @Override
    public CheckoutResponseDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyCartException("Cart not found for user"));

        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            if (variant.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for variant " + variant.getVariantId()
                );
            }
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
            total = total.add(itemSubtotal);
        }
        Sale sale = Sale.builder()
                .idUser(userId)
                .subtotal(subtotal)
                .total(total)
                .status(SaleStatus.PENDING)
                .build();

        sale = saleRepository.save(sale);

        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            SaleDetail detail = SaleDetail.builder()
                    .sale(sale)
                    .productVariant(variant)
                    .quantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();

            saleDetailRepository.save(detail);
        }

        cartItemRepository.deleteAllByCart_CartId(cart.getCartId());

        return CheckoutResponseDTO.builder()
                .idSale(sale.getIdSale())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .build();
    }

    @Override
    public SaleResponseDTO confirmPayment(Long saleId, Long userId, String statusReport) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found for user"
                ));
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new InvalidSaleStatusException(
                "Sale status must be PENDING to confirm payment"
            );
        }

        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            if (variant.getStock() < detail.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for variant " + variant.getVariantId()
                );
            }
        }
        List<ProductVariant> variantsToUpdate = new ArrayList<>();
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            variant.setStock(variant.getStock() - detail.getQuantity());
            variantsToUpdate.add(variant);
        }
        productVariantRepository.saveAll(variantsToUpdate);
        sale.setStatus(SaleStatus.PAID);
        java.util.Optional<com.thegroup.pf_sgr.model.User> optUser = userRepository.findById(sale.getIdUser());
        if (optUser.isPresent()) {
            sale.setShippingAddress(optUser.get().getAddress());
        }
        if (statusReport != null && !statusReport.trim().isEmpty()) {
            sale.setStatusReport(statusReport);
        }
        sale = saleRepository.save(sale);

        // Extract payment method from statusReport or default to "UNKNOWN"
        String paymentMethod = "UNKNOWN";
        if (statusReport != null && statusReport.contains("método: ")) {
            paymentMethod = statusReport.substring(statusReport.indexOf("método: ") + 8).trim();
        }

        com.thegroup.pf_sgr.model.PaymentStatus approvedStatus = paymentStatusRepository.findByStatusName("APPROVED")
                .orElseThrow(() -> new RuntimeException("Payment status APPROVED not found in DB"));

        com.thegroup.pf_sgr.model.Payment payment = com.thegroup.pf_sgr.model.Payment.builder()
                .sale(sale)
                .paymentMethod(paymentMethod)
                .amount(sale.getTotal())
                .status(approvedStatus)
                .build();
        paymentRepository.save(payment);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponseDTO cancelOrder(Long saleId, Long userId, String statusReport) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found for user"
                ));
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new InvalidSaleStatusException(
                "Sale status must be PENDING to cancel"
            );
        }
        sale.setStatus(SaleStatus.CANCELLED);
        if (statusReport != null && !statusReport.trim().isEmpty()) {
            sale.setStatusReport(statusReport);
        }
        sale = saleRepository.save(sale);
        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponseDTO refundOrder(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found"
                ));
        if (sale.getStatus() != SaleStatus.PAID) {
            throw new InvalidSaleStatusException(
                "Sale status must be PAID to refund"
            );
        }
        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);
        List<ProductVariant> variantsToUpdate = new ArrayList<>();
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            variant.setStock(variant.getStock() + detail.getQuantity());
            variantsToUpdate.add(variant);
        }
        productVariantRepository.saveAll(variantsToUpdate);

        sale.setStatus(SaleStatus.REFUNDED);
        sale = saleRepository.save(sale);

        java.util.Optional<com.thegroup.pf_sgr.model.Payment> paymentOpt = paymentRepository.findBySale_IdSale(saleId);
        if (paymentOpt.isPresent()) {
            com.thegroup.pf_sgr.model.Payment payment = paymentOpt.get();
            com.thegroup.pf_sgr.model.PaymentStatus refundedStatus = paymentStatusRepository.findByStatusName("REFUNDED")
                    .orElseThrow(() -> new RuntimeException("Payment status REFUNDED not found in DB"));
            payment.setStatus(refundedStatus);
            paymentRepository.save(payment);
        }

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public List<SaleResponseDTO> getSalesByUser(Long userId) {
        List<Sale> sales = saleRepository.findByIdUser(userId);
        
        return sales.stream().map(sale -> {
            List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(sale.getIdSale());
            return buildSaleResponseDTO(sale, details);
        }).collect(Collectors.toList());
    }

    @Override
    public SaleResponseDTO getSaleDetail(Long saleId, Long userId) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found for user"
                ));
        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);

        return buildSaleResponseDTO(sale, details);
    }

    private SaleResponseDTO buildSaleResponseDTO(Sale sale, List<SaleDetail> details) {
        List<SaleDetailDTO> detailDTOs = details.stream()
                .map(detail -> SaleDetailDTO.builder()
                        .idVariant(detail.getProductVariant().getVariantId())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .totalPrice(detail.getTotalPrice())
                        .productName(detail.getProductVariant().getProduct().getName())
                        .color(detail.getProductVariant().getColor() != null ? detail.getProductVariant().getColor().getName() : "N/A")
                        .size(detail.getProductVariant().getSize() != null ? detail.getProductVariant().getSize().getName() : "N/A")
                        .build())
                .collect(Collectors.toList());

        com.thegroup.pf_sgr.dto.PaymentDTO paymentDTO = null;
        java.util.Optional<com.thegroup.pf_sgr.model.Payment> paymentOpt = paymentRepository.findBySale_IdSale(sale.getIdSale());
        if (paymentOpt.isPresent()) {
            com.thegroup.pf_sgr.model.Payment payment = paymentOpt.get();
            paymentDTO = com.thegroup.pf_sgr.dto.PaymentDTO.builder()
                    .idPayment(payment.getIdPayment())
                    .paymentMethod(payment.getPaymentMethod())
                    .amount(payment.getAmount())
                    .paymentDate(payment.getPaymentDate())
                    .status(payment.getStatus().getStatusName())
                    .build();
        }

        return SaleResponseDTO.builder()
                .idSale(sale.getIdSale())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .statusReport(sale.getStatusReport())
                .shippingAddress(sale.getShippingAddress())
                .payment(paymentDTO)
                .details(detailDTOs)
                .build();
    }
}


