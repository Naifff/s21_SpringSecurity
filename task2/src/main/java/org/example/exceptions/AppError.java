package org.example.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppError {
	private int status;
	private String message;
	private Date timestamp;

	public AppError(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = new Date();
	}
}