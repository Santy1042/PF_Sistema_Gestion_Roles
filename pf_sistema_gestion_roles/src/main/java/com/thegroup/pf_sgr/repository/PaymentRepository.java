package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySale_IdSale(Long idSale);
}
