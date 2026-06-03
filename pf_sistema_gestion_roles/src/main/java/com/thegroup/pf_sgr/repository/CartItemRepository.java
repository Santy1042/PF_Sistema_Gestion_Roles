package com.thegroup.pf_sgr.repository;

import com.thegroup.pf_sgr.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_CartId(Long cartId);
    void deleteAllByCart_CartId(Long cartId);
}
