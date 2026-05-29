package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByIdSaleAndIdUser(Long idSale, Long idUser);
    List<Sale> findByIdUser(Long idUser);
}
