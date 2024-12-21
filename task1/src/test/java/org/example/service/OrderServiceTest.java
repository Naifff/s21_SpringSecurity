package org.example.service;

import org.example.dto.OrderDTO;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.enums.OrderStatus;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private OrderService orderService;

	private User testUser;
	private Order testOrder;
	private OrderDTO testOrderDTO;

	@BeforeEach
	void setUp() {
		// Подготовка тестовых данных
		testUser = User.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.build();

		testOrder = Order.builder()
				.id(1L)
				.description("Test Order")
				.amount(new BigDecimal("100.00"))
				.status(OrderStatus.NEW)
				.user(testUser)
				.build();

		testOrderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("100.00"))
				.userId(1L)
				.build();
	}

	@Test
	void whenCreateOrder_thenReturnOrder() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

		Order created = orderService.createOrder(testOrderDTO);

		assertThat(created).isNotNull();
		assertThat(created.getId()).isEqualTo(1L);
		assertThat(created.getStatus()).isEqualTo(OrderStatus.NEW);
	}

	@Test
	void whenCreateOrderWithNonExistentUser_thenThrowException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.createOrder(testOrderDTO);
		});
	}
}