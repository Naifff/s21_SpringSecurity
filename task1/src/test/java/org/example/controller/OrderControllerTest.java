package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.TestSecurityConfig;
import org.example.dto.OrderDTO;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.enums.OrderStatus;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.ResourceNotFoundException;
import org.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {OrderController.class, TestSecurityConfig.class, GlobalExceptionHandler.class})
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void whenGetAllOrders_thenReturnOrdersList() throws Exception {
		List<Order> orders = Arrays.asList(
				createTestOrder(1L, "Order 1", new BigDecimal("100.00"), 1L),
				createTestOrder(2L, "Order 2", new BigDecimal("200.00"), 2L)
		);

		given(orderService.getAllOrders()).willReturn(orders);

		mockMvc.perform(get("/api/orders"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[1].id", is(2)));
	}

	@Test
	void whenGetUserOrders_thenReturnUserOrdersList() throws Exception {
		List<Order> orders = Arrays.asList(
				createTestOrder(1L, "Order 1", new BigDecimal("100.00"), 1L),
				createTestOrder(2L, "Order 2", new BigDecimal("200.00"), 1L)
		);

		given(orderService.getOrdersByUserId(1L)).willReturn(orders);

		mockMvc.perform(get("/api/users/1/orders"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[1].id", is(2)));
	}

	@Test
	void whenGetOrderById_thenReturnOrder() throws Exception {
		Order order = createTestOrder(1L, "Test Order", new BigDecimal("100.00"), 1L);
		given(orderService.getOrderById(1L)).willReturn(order);

		mockMvc.perform(get("/api/orders/1"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.description", is("Test Order")));
	}

	@Test
	void whenGetNonExistentOrder_thenReturn404() throws Exception {
		given(orderService.getOrderById(anyLong()))
				.willThrow(new ResourceNotFoundException("Order not found"));

		mockMvc.perform(get("/api/orders/1"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Order not found")));
	}

	@Test
	void whenCreateOrder_thenReturnCreatedOrder() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("99.99"))
				.userId(1L)
				.build();

		Order createdOrder = createTestOrder(1L, "Test Order", new BigDecimal("99.99"), 1L);
		given(orderService.createOrder(any(OrderDTO.class))).willReturn(createdOrder);

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.description", is("Test Order")));
	}

	@Test
	void whenUpdateOrder_thenReturnUpdatedOrder() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Updated Order")
				.amount(new BigDecimal("150.00"))
				.userId(1L)
				.status(OrderStatus.PROCESSING)
				.build();

		Order updatedOrder = createTestOrder(1L, "Updated Order", new BigDecimal("150.00"), 1L);
		updatedOrder.setStatus(OrderStatus.PROCESSING);
		given(orderService.updateOrder(anyLong(), anyLong(), any(OrderDTO.class)))
				.willReturn(updatedOrder);

		mockMvc.perform(put("/api/users/1/orders/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", is("Updated Order")))
				.andExpect(jsonPath("$.status", is("PROCESSING")));
	}

	@Test
	void whenUpdateOrderStatus_thenReturnUpdatedOrder() throws Exception {
		Order updatedOrder = createTestOrder(1L, "Test Order", new BigDecimal("100.00"), 1L);
		updatedOrder.setStatus(OrderStatus.COMPLETED);
		given(orderService.updateOrderStatus(anyLong(), anyLong(), any(OrderStatus.class)))
				.willReturn(updatedOrder);

		mockMvc.perform(patch("/api/users/1/orders/1/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content("\"COMPLETED\""))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status", is("COMPLETED")));
	}

	@Test
	void whenUpdateNonExistentOrder_thenReturn404() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Updated Order")
				.amount(new BigDecimal("150.00"))
				.userId(1L)
				.build();

		given(orderService.updateOrder(anyLong(), anyLong(), any(OrderDTO.class)))
				.willThrow(new ResourceNotFoundException("Order not found"));

		mockMvc.perform(put("/api/users/1/orders/999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Order not found")));
	}

	@Test
	void whenDeleteOrder_thenReturn204() throws Exception {
		doNothing().when(orderService).deleteOrder(anyLong(), anyLong());

		mockMvc.perform(delete("/api/users/1/orders/1"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNoContent());
	}

	@Test
	void whenDeleteNonExistentOrder_thenReturn404() throws Exception {
		doThrow(new ResourceNotFoundException("Order not found"))
				.when(orderService).deleteOrder(anyLong(), anyLong());

		mockMvc.perform(delete("/api/users/1/orders/999"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Order not found")));
	}

	@Test
	void whenCreateOrderWithoutUserId_thenReturnBadRequest() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("99.99"))
				.userId(null)
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.userId", is("User ID is required")));
	}

	@Test
	void whenCreateOrderWithNegativeAmount_thenReturnBadRequest() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("-50.00"))
				.userId(1L)
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.amount", is("Amount must be positive")));
	}

	@Test
	void whenCreateOrderWithEmptyDescription_thenReturnBadRequest() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("")
				.amount(new BigDecimal("99.99"))
				.userId(1L)
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.description", is("Description is required")));
	}

	private Order createTestOrder(Long id, String description, BigDecimal amount, Long userId) {
		User user = User.builder()
				.id(userId)
				.name("Test User")
				.email("test@example.com")
				.build();

		return Order.builder()
				.id(id)
				.description(description)
				.amount(amount)
				.status(OrderStatus.NEW)
				.user(user)
				.build();
	}
}