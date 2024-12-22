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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
	void whenGetAllOrders_thenReturnOrdersList() {
		List<Order> orders = Arrays.asList(testOrder);
		when(orderRepository.findAll()).thenReturn(orders);

		List<Order> found = orderService.getAllOrders();

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getId()).isEqualTo(testOrder.getId());
	}

	@Test
	void whenGetOrdersByUserId_thenReturnUserOrders() {
		List<Order> orders = Arrays.asList(testOrder);
		when(userRepository.existsById(anyLong())).thenReturn(true);
		when(orderRepository.findByUserId(anyLong())).thenReturn(orders);

		List<Order> found = orderService.getOrdersByUserId(1L);

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getId()).isEqualTo(testOrder.getId());
	}

	@Test
	void whenGetOrdersByNonExistentUserId_thenThrowException() {
		when(userRepository.existsById(anyLong())).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.getOrdersByUserId(999L);
		});
	}

	@Test
	void whenGetOrderById_thenReturnOrder() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

		Order found = orderService.getOrderById(1L);

		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(testOrder.getId());
	}

	@Test
	void whenGetNonExistentOrderById_thenThrowException() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.getOrderById(999L);
		});
	}

	@Test
	void whenCreateOrder_thenReturnOrder() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

		Order created = orderService.createOrder(testOrderDTO);

		assertThat(created).isNotNull();
		assertThat(created.getDescription()).isEqualTo(testOrderDTO.getDescription());
		assertThat(created.getStatus()).isEqualTo(OrderStatus.NEW);
	}

	@Test
	void whenCreateOrderForNonExistentUser_thenThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.createOrder(testOrderDTO);
		});
	}

	@Test
	void whenUpdateOrder_thenReturnUpdatedOrder() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(true);
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
		when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

		OrderDTO updateDTO = OrderDTO.builder()
				.description("Updated Order")
				.amount(new BigDecimal("150.00"))
				.userId(1L)
				.status(OrderStatus.PROCESSING)
				.build();

		Order updated = orderService.updateOrder(1L, 1L, updateDTO);

		assertThat(updated).isNotNull();
		verify(orderRepository).save(any(Order.class));
	}

	@Test
	void whenUpdateNonExistentOrder_thenThrowException() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.updateOrder(999L, 1L, testOrderDTO);
		});
	}

	@Test
	void whenDeleteOrder_thenCallRepository() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(true);
		doNothing().when(orderRepository).deleteById(anyLong());

		orderService.deleteOrder(1L, 1L);

		verify(orderRepository).deleteById(1L);
	}

	@Test
	void whenDeleteNonExistentOrder_thenThrowException() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.deleteOrder(999L, 1L);
		});
	}

	@Test
	void whenUpdateOrderStatus_thenReturnUpdatedOrder() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(true);
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
		when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

		Order updated = orderService.updateOrderStatus(1L, 1L, OrderStatus.COMPLETED);

		assertThat(updated).isNotNull();
		verify(orderRepository).save(any(Order.class));
	}

	@Test
	void whenUpdateStatusOfNonExistentOrder_thenThrowException() {
		when(orderRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.updateOrderStatus(999L, 1L, OrderStatus.COMPLETED);
		});
	}
}