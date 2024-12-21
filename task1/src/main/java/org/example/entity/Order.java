package org.example.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.OrderStatus;
import org.example.dto.Views;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.UserDetails.class)
	private Long id;

	@JsonView(Views.UserDetails.class)
	@NotBlank(message = "Description is required")
	private String description;

	@JsonView(Views.UserDetails.class)
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	private BigDecimal amount;

	@JsonView(Views.UserDetails.class)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
