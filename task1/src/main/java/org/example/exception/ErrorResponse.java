package org.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	private int status;
	private String message;
	private Map<String, String> errors;

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
	}
}