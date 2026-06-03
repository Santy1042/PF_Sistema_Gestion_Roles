package com.thegroup.pf_sgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thegroup.pf_sgr.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
