package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {
    List<SaleDetail> findByIdSale(Long idSale);
}
