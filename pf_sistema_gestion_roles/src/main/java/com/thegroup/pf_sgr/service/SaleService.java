package com.thegroup.pf_sgr.service;

import com.thegroup.pf_sgr.dto.CheckoutResponse;
import com.thegroup.pf_sgr.dto.PaymentResponse;
import com.thegroup.pf_sgr.dto.SaleDetailResponse;
import com.thegroup.pf_sgr.dto.SaleResponse;
import com.thegroup.pf_sgr.dto.AdminSaleUpdateRequest;
import com.thegroup.pf_sgr.exception.EmptyCartException;
import com.thegroup.pf_sgr.exception.InsufficientStockException;
import com.thegroup.pf_sgr.exception.InvalidSaleStatusException;
import com.thegroup.pf_sgr.exception.SaleNotFoundException;
import com.thegroup.pf_sgr.interfaces.ISaleService;
import com.thegroup.pf_sgr.model.Cart;
import com.thegroup.pf_sgr.model.CartItem;
import com.thegroup.pf_sgr.model.Payment;
import com.thegroup.pf_sgr.model.PaymentStatus;
import com.thegroup.pf_sgr.model.Product;
import com.thegroup.pf_sgr.model.ProductVariant;
import com.thegroup.pf_sgr.model.Sale;
import com.thegroup.pf_sgr.model.SaleDetail;
import com.thegroup.pf_sgr.model.SaleStatus;
import com.thegroup.pf_sgr.model.User;
import com.thegroup.pf_sgr.repository.CartItemRepository;
import com.thegroup.pf_sgr.repository.CartRepository;
import com.thegroup.pf_sgr.repository.PaymentRepository;
import com.thegroup.pf_sgr.repository.PaymentStatusRepository;
import com.thegroup.pf_sgr.repository.ProductVariantRepository;
import com.thegroup.pf_sgr.repository.SaleDetailRepository;
import com.thegroup.pf_sgr.repository.SaleRepository;
import com.thegroup.pf_sgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleService implements ISaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final UserRepository userRepository;

    @Override
    public CheckoutResponse createOrder(Long userId) {
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

            variant.setStock(variant.getStock() - item.getQuantity());
            productVariantRepository.save(variant);

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

        return CheckoutResponse.builder()
                .idSale(sale.getIdSale())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .build();
    }

    @Override
    public SaleResponse confirmPayment(Long saleId, Long userId, String statusReport) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found for user"));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new InvalidSaleStatusException("Sale status must be PENDING to confirm payment");
        }

        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);

        sale.setStatus(SaleStatus.PAID);

        Optional<User> optUser = userRepository.findById(sale.getIdUser());
        if (optUser.isPresent()) {
            sale.setShippingAddress(optUser.get().getAddress());
        }
        if (statusReport != null && !statusReport.trim().isEmpty()) {
            sale.setStatusReport(statusReport);
        }
        sale = saleRepository.save(sale);

        String paymentMethod = "UNKNOWN";
        if (statusReport != null && statusReport.contains("método: ")) {
            paymentMethod = statusReport.substring(statusReport.indexOf("método: ") + 8).trim();
        }

        PaymentStatus approvedStatus = paymentStatusRepository.findByStatusName("APPROVED")
                .orElseThrow(() -> new RuntimeException("Payment status APPROVED not found in DB"));

        Payment payment = Payment.builder()
                .sale(sale)
                .paymentMethod(paymentMethod)
                .amount(sale.getTotal())
                .status(approvedStatus)
                .build();
        paymentRepository.save(payment);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponse cancelOrder(Long saleId, Long userId, String statusReport) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found for user"));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new InvalidSaleStatusException("Sale status must be PENDING to cancel");
        }

        sale.setStatus(SaleStatus.CANCELLED);
        if (statusReport != null && !statusReport.trim().isEmpty()) {
            sale.setStatusReport(statusReport);
        }
        sale = saleRepository.save(sale);

        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);
        List<ProductVariant> variantsToUpdate = new ArrayList<>();
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            variant.setStock(variant.getStock() + detail.getQuantity());
            variantsToUpdate.add(variant);
        }
        productVariantRepository.saveAll(variantsToUpdate);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponse refundOrder(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        if (sale.getStatus() != SaleStatus.PAID) {
            throw new InvalidSaleStatusException("Sale status must be PAID to refund");
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

        List<Payment> payments = paymentRepository.findAllBySale_IdSale(saleId);
        for (Payment payment : payments) {
            PaymentStatus refundedStatus = paymentStatusRepository.findByStatusName("REFUNDED")
                    .orElseThrow(() -> new RuntimeException("Payment status REFUNDED not found in DB"));
            payment.setStatus(refundedStatus);
            paymentRepository.save(payment);
        }

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public List<SaleResponse> getSalesByUser(Long userId) {
        return saleRepository.findByIdUser(userId).stream()
                .map(sale -> buildSaleResponseDTO(sale, sale.getDetails()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAllWithDetails().stream()
                .map(sale -> buildSaleResponseDTO(sale, sale.getDetails()))
                .collect(Collectors.toList());
    }

    @Override
    public SaleResponse updateSaleAdmin(Long saleId, AdminSaleUpdateRequest request) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        if (request.getShippingAddress() != null && !request.getShippingAddress().trim().isEmpty()) {
            sale.setShippingAddress(request.getShippingAddress());
        }

        if (request.getStatusReport() != null && !request.getStatusReport().trim().isEmpty()) {
            sale.setStatusReport(request.getStatusReport());
        }

        if (request.getStatus() != null && !request.getStatus().equals(sale.getStatus().name())) {
            SaleStatus newStatus = SaleStatus.valueOf(request.getStatus().toUpperCase());
            SaleStatus currentStatus = sale.getStatus();

            List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);

            if (newStatus == SaleStatus.PAID && currentStatus == SaleStatus.PENDING) {

                if (sale.getShippingAddress() == null) {
                    Sale saleRef = sale;
                    userRepository.findById(sale.getIdUser())
                            .ifPresent(u -> saleRef.setShippingAddress(u.getAddress()));
                }

                sale.setStatus(SaleStatus.PAID);
                sale = saleRepository.save(sale);

                PaymentStatus approvedStatus = paymentStatusRepository.findByStatusName("APPROVED")
                        .orElseThrow(() -> new RuntimeException("Payment status APPROVED not found"));
                Payment payment = Payment.builder()
                        .sale(sale)
                        .paymentMethod("ADMIN")
                        .amount(sale.getTotal())
                        .status(approvedStatus)
                        .build();
                paymentRepository.save(payment);

                return buildSaleResponseDTO(sale, details);

            } else if (newStatus == SaleStatus.CANCELLED && currentStatus == SaleStatus.PENDING) {

                for (SaleDetail detail : details) {
                    ProductVariant variant = detail.getProductVariant();
                    variant.setStock(variant.getStock() + detail.getQuantity());
                }
                productVariantRepository.saveAll(
                    details.stream().map(SaleDetail::getProductVariant).collect(Collectors.toList())
                );
                sale.setStatus(SaleStatus.CANCELLED);
                sale = saleRepository.save(sale);
                return buildSaleResponseDTO(sale, details);

            } else if (newStatus == SaleStatus.REFUNDED && currentStatus == SaleStatus.PAID) {

                List<ProductVariant> toUpdate = new ArrayList<>();
                for (SaleDetail detail : details) {
                    ProductVariant variant = detail.getProductVariant();
                    variant.setStock(variant.getStock() + detail.getQuantity());
                    toUpdate.add(variant);
                }
                productVariantRepository.saveAll(toUpdate);

                sale.setStatus(SaleStatus.REFUNDED);
                sale = saleRepository.save(sale);

                List<Payment> paymentsList = paymentRepository.findAllBySale_IdSale(saleId);
                for (Payment payment : paymentsList) {
                    PaymentStatus refundedStatus = paymentStatusRepository.findByStatusName("REFUNDED")
                            .orElseThrow(() -> new RuntimeException("Payment status REFUNDED not found"));
                    payment.setStatus(refundedStatus);
                    paymentRepository.save(payment);
                }

                return buildSaleResponseDTO(sale, details);

            } else {
                sale.setStatus(newStatus);
            }
        }

        sale = saleRepository.save(sale);
        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);
        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponse getSaleDetail(Long saleId, Long userId) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found for user"));
        List<SaleDetail> details = saleDetailRepository.findBySale_IdSale(saleId);
        return buildSaleResponseDTO(sale, details);
    }

    private SaleResponse buildSaleResponseDTO(Sale sale, List<SaleDetail> details) {
        List<SaleDetailResponse> detailDTOs = details.stream()
                .map(detail -> SaleDetailResponse.builder()
                        .idVariant(detail.getProductVariant().getVariantId())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .totalPrice(detail.getTotalPrice())
                        .productName(detail.getProductVariant().getProduct().getName())
                        .color(detail.getProductVariant().getColor() != null
                            ? detail.getProductVariant().getColor().getName() : "N/A")
                        .size(detail.getProductVariant().getSize() != null
                            ? detail.getProductVariant().getSize().getName() : "N/A")
                        .build())
                .collect(Collectors.toList());

        List<Payment> paymentsEntity = paymentRepository.findAllBySale_IdSale(sale.getIdSale());
        List<PaymentResponse> paymentDTOList = paymentsEntity.stream().map(payment -> PaymentResponse.builder()
                .idPayment(payment.getIdPayment())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().getStatusName())
                .build()).collect(Collectors.toList());

        PaymentResponse paymentDTO = null;
        if (!paymentDTOList.isEmpty()) {
            paymentDTO = paymentDTOList.get(paymentDTOList.size() - 1);
        }

        return SaleResponse.builder()
                .idSale(sale.getIdSale())
                .idUser(sale.getIdUser())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .statusReport(sale.getStatusReport())
                .shippingAddress(sale.getShippingAddress())
                .payment(paymentDTO)
                .payments(paymentDTOList)
                .details(detailDTOs)
                .build();
    }
}
