package org.example.repository;

import org.example.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserId(Long userId);

	// Добавляем метод для проверки, принадлежит ли заказ пользователю
	@Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
	boolean existsByIdAndUserId(Long orderId, Long userId);
}
