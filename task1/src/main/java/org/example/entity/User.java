package org.example.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.Views;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.UserSummary.class)
	private Long id;

	@JsonView(Views.UserSummary.class)
	@NotBlank(message = "Name is required")
	private String name;

	@JsonView(Views.UserSummary.class)
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@JsonView(Views.UserDetails.class)
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<>();
}
