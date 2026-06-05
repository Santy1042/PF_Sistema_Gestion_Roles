package com.thegroup.pf_sgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thegroup.pf_sgr.model.ProductVariant;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    @Modifying
    @Query("UPDATE ProductVariant p SET p.stock = p.stock - :quantity WHERE p.variantId = :variantId AND p.stock >= :quantity")
    int reduceStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE ProductVariant p SET p.stock = p.stock + :quantity WHERE p.variantId = :variantId")
    int increaseStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);
}
