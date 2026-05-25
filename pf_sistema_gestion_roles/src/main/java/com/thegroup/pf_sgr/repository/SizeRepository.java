package com.thegroup.pf_sgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thegroup.pf_sgr.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

}
