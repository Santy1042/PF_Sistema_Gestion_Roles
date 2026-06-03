package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {
    Optional<PaymentStatus> findByStatusName(String statusName);
}
