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

    @Override
    public CheckoutResponseDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyCartException("Cart not found for user"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getCartId());
        
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

        cartItemRepository.deleteAllByCartId(cart.getCartId());

        return CheckoutResponseDTO.builder()
                .idSale(sale.getIdSale())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .build();
    }

    @Override
    public SaleResponseDTO confirmPayment(Long saleId, Long userId) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found for user"
                ));
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new InvalidSaleStatusException(
                "Sale status must be PENDING to confirm payment"
            );
        }

        List<SaleDetail> details = saleDetailRepository.findByIdSale(saleId);
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            if (variant.getStock() < detail.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for variant " + variant.getVariantId()
                );
            }
        }
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            variant.setStock(variant.getStock() - detail.getQuantity());
            productVariantRepository.save(variant);
        }
        sale.setStatus(SaleStatus.PAID);
        sale = saleRepository.save(sale);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public SaleResponseDTO cancelOrder(Long saleId, Long userId) {
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
        sale = saleRepository.save(sale);
        List<SaleDetail> details = saleDetailRepository.findByIdSale(saleId);

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
        List<SaleDetail> details = saleDetailRepository.findByIdSale(saleId);
        for (SaleDetail detail : details) {
            ProductVariant variant = detail.getProductVariant();
            variant.setStock(variant.getStock() + detail.getQuantity());
            productVariantRepository.save(variant);
        }

        sale.setStatus(SaleStatus.REFUNDED);
        sale = saleRepository.save(sale);

        return buildSaleResponseDTO(sale, details);
    }

    @Override
    public List<SaleResponseDTO> getSalesByUser(Long userId) {
        List<Sale> sales = saleRepository.findByIdUser(userId);
        
        return sales.stream().map(sale -> {
            List<SaleDetail> details = saleDetailRepository.findByIdSale(sale.getIdSale());
            return buildSaleResponseDTO(sale, details);
        }).collect(Collectors.toList());
    }

    @Override
    public SaleResponseDTO getSaleDetail(Long saleId, Long userId) {
        Sale sale = saleRepository.findByIdSaleAndIdUser(saleId, userId)
                .orElseThrow(() -> new SaleNotFoundException(
                    "Sale not found for user"
                ));
        List<SaleDetail> details = saleDetailRepository.findByIdSale(saleId);

        return buildSaleResponseDTO(sale, details);
    }

    private SaleResponseDTO buildSaleResponseDTO(Sale sale, List<SaleDetail> details) {
        List<SaleDetailDTO> detailDTOs = details.stream()
                .map(detail -> SaleDetailDTO.builder()
                        .idVariant(detail.getProductVariant().getVariantId())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .totalPrice(detail.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return SaleResponseDTO.builder()
                .idSale(sale.getIdSale())
                .status(sale.getStatus())
                .subtotal(sale.getSubtotal())
                .total(sale.getTotal())
                .saleDate(sale.getSaleDate())
                .details(detailDTOs)
                .build();
    }
}
