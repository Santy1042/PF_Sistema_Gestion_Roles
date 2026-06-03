package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.Sale;
import com.thegroup.pf_sgr.model.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByIdSaleAndIdUser(Long idSale, Long idUser);

    @Query("SELECT DISTINCT s FROM Sale s LEFT JOIN FETCH s.details WHERE s.idUser = :idUser")
    List<Sale> findByIdUser(Long idUser);

    @Query("SELECT DISTINCT s FROM Sale s LEFT JOIN FETCH s.details")
    List<Sale> findAllWithDetails();

    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.status IN :statuses")
    BigDecimal sumTotalByStatuses(@Param("statuses") List<SaleStatus> statuses);
}
