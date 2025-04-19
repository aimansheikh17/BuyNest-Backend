package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {

	
	@Query("Select c from CartItem c where c.user.user_id=:user_id and c.product.productId=:productId")
	Optional<CartItem> findByUserAndProduct(int user_id, int productId);
	
	@Query("Select COALESCE(SUM(c.quantity),0) from CartItem c where c.user.user_id=:user_id")
	int CountTotalItems(int user_id);
	
	@Query("Select c from CartItem c Join fetch c.product p left join fetch p.images pi where c.user.user_id=:user_id")
	List<CartItem> findCartItemsWithProductDetails(int user_id);
	
	
	@Modifying
	@Transactional
	@Query("Update CartItem c SET c.quantity= :quantity where c.id = :cartItemId")
	public int updateCartItemQuantity(int cartItemId, int quantity);


	

	@Modifying
	@Transactional
    @Query("Delete from CartItem c where c.user.user_id=:user_id And c.product.productId=:productId")
	public void deleteCartItem(int user_id, int productId);
	

	@Modifying
	@Transactional
	@Query("Delete from CartItem c where c.user.user_id=:user_id")
	public void deleteAllCartItemByUser(int user_id);
}
