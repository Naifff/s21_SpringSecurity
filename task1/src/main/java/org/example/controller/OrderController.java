package org.example.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.dto.Views;
import org.example.entity.Order;
import org.example.enums.OrderStatus;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	// Получить все заказы (для администратора)
	@GetMapping("/orders")
	@JsonView(Views.UserDetails.class)
	public List<Order> getAllOrders() {
		return orderService.getAllOrders();
	}

	// Получить заказы конкретного пользователя
	@GetMapping("/users/{userId}/orders")
	@JsonView(Views.UserDetails.class)
	public List<Order> getUserOrders(@PathVariable Long userId) {
		return orderService.getOrdersByUserId(userId);
	}

	// Получить конкретный заказ
	@GetMapping("/orders/{orderId}")
	@JsonView(Views.UserDetails.class)
	public Order getOrderById(@PathVariable Long orderId) {
		return orderService.getOrderById(orderId);
	}

	// Создать новый заказ
	@PostMapping("/users/{userId}/orders")
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(Views.UserDetails.class)
	public Order createOrder(@PathVariable Long userId, @Valid @RequestBody OrderDTO orderDTO) {
		// Проверяем, совпадает ли userId из пути с userId в DTO
		if (!userId.equals(orderDTO.getUserId())) {
			throw new IllegalArgumentException("User ID in path must match User ID in request body");
		}
		return orderService.createOrder(orderDTO);
	}

	// Обновить существующий заказ
	@PutMapping("/users/{userId}/orders/{orderId}")
	@JsonView(Views.UserDetails.class)
	public Order updateOrder(
			@PathVariable Long userId,
			@PathVariable Long orderId,
			@Valid @RequestBody OrderDTO orderDTO) {
		return orderService.updateOrder(orderId, userId, orderDTO);
	}

	// Обновить статус заказа
	@PatchMapping("/users/{userId}/orders/{orderId}/status")
	@JsonView(Views.UserDetails.class)
	public Order updateOrderStatus(
			@PathVariable Long userId,
			@PathVariable Long orderId,
			@RequestBody OrderStatus newStatus) {
		return orderService.updateOrderStatus(orderId, userId, newStatus);
	}

	// Удалить заказ
	@DeleteMapping("/users/{userId}/orders/{orderId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable Long userId, @PathVariable Long orderId) {
		orderService.deleteOrder(orderId, userId);
	}
}
