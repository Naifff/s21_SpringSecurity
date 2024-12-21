package org.example.init;

import org.example.entity.Order;
import org.example.entity.User;
import org.example.enums.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public void run(String... args) throws Exception {
		// Create some users
		User user1 = User.builder()
				.name("John Doe")
				.email("john.doe@example.com")
				.build();

		User user2 = User.builder()
				.name("Jane Smith")
				.email("jane.smith@example.com")
				.build();

		// Save users to the repository
		userRepository.saveAll(Arrays.asList(user1, user2));

		// Create some orders
		Order order1 = Order.builder()
				.description("Order 1 description")
				.amount(new BigDecimal("100.00"))
				.status(OrderStatus.NEW)
				.user(user1)
				.build();

		Order order2 = Order.builder()
				.description("Order 2 description")
				.amount(new BigDecimal("200.00"))
				.status(OrderStatus.PROCESSING)
				.user(user1)
				.build();

		Order order3 = Order.builder()
				.description("Order 3 description")
				.amount(new BigDecimal("300.00"))
				.status(OrderStatus.COMPLETED)
				.user(user2)
				.build();

		// Save orders to the repository
		orderRepository.saveAll(Arrays.asList(order1, order2, order3));
	}
}