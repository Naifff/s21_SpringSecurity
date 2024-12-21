package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.OrderStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
	private Long id;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	private BigDecimal amount;

	@NotNull(message = "User ID is required")
	private Long userId;

	private OrderStatus status;
}
