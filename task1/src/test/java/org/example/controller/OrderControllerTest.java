package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.TestSecurityConfig;
import org.example.dto.OrderDTO;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.enums.OrderStatus;
import org.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;  // Для HTTP POST запросов

import java.math.BigDecimal;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {OrderController.class, TestSecurityConfig.class})
class OrderControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;  // Spring Boot автоматически настроит этот маппер

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void whenCreateOrder_thenReturnCreatedOrder() throws Exception {
		// Подготавливаем тестовые данные
		Long userId = 1L;
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("99.99"))
				.userId(userId)  // Устанавливаем userId
				.build();

		Order createdOrder = Order.builder()
				.id(1L)
				.description(orderDTO.getDescription())
				.amount(orderDTO.getAmount())
				.status(OrderStatus.NEW)
				.user(User.builder().id(userId).build())  // Добавляем связь с пользователем
				.build();

		// Настраиваем поведение мока
		given(orderService.createOrder(any(OrderDTO.class)))
				.willReturn(createdOrder);

		// Выполняем тестовый запрос и проверяем результат
		mockMvc.perform(post("/api/users/{userId}/orders", userId)  // Используем переменную пути
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.description", is("Test Order")))
				.andExpect(jsonPath("$.amount", is(99.99)))
				.andExpect(jsonPath("$.status", is("NEW")));
	}

	@Test
//	@WithMockUser(username = "user", roles = {"USER"})
	void whenCreateOrderWithoutUserId_thenReturnBadRequest() throws Exception {
		// Создаем DTO без userId
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("99.99"))
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.userId", is("User ID is required")));
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void whenCreateOrderWithNegativeAmount_thenReturnBadRequest() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("Test Order")
				.amount(new BigDecimal("-50.00"))
				.userId(1L)
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.amount", is("Amount must be positive")));
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void whenCreateOrderWithEmptyDescription_thenReturnBadRequest() throws Exception {
		OrderDTO orderDTO = OrderDTO.builder()
				.description("")
				.amount(new BigDecimal("99.99"))
				.userId(1L)
				.build();

		mockMvc.perform(post("/api/users/1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.description", is("Description is required")));
	}
}
