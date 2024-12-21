package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.enums.OrderStatus;
import org.example.dto.OrderDTO;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public List<Order> getOrdersByUserId(Long userId) {
		// Проверяем существование пользователя
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}
		return orderRepository.findByUserId(userId);
	}

	public Order getOrderById(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
	}

	public Order createOrder(OrderDTO orderDTO) {
		// Находим пользователя
		User user = userRepository.findById(orderDTO.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderDTO.getUserId()));

		// Создаем новый заказ
		Order order = Order.builder()
				.description(orderDTO.getDescription())
				.amount(orderDTO.getAmount())
				.status(OrderStatus.NEW) // Устанавливаем начальный статус
				.user(user)
				.build();

		return orderRepository.save(order);
	}

	public Order updateOrder(Long orderId, Long userId, OrderDTO orderDTO) {
		// Проверяем, существует ли заказ и принадлежит ли он указанному пользователю
		if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
			throw new ResourceNotFoundException("Order not found or doesn't belong to user");
		}

		Order order = getOrderById(orderId);
		order.setDescription(orderDTO.getDescription());
		order.setAmount(orderDTO.getAmount());
		order.setStatus(orderDTO.getStatus());

		return orderRepository.save(order);
	}

	public void deleteOrder(Long orderId, Long userId) {
		// Проверяем, существует ли заказ и принадлежит ли он указанному пользователю
		if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
			throw new ResourceNotFoundException("Order not found or doesn't belong to user");
		}

		orderRepository.deleteById(orderId);
	}

	public Order updateOrderStatus(Long orderId, Long userId, OrderStatus newStatus) {
		if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
			throw new ResourceNotFoundException("Order not found or doesn't belong to user");
		}

		Order order = getOrderById(orderId);
		order.setStatus(newStatus);
		return orderRepository.save(order);
	}
}
