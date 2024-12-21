package org.example.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
	NEW("Новый заказ"),
	PROCESSING("В обработке"),
	COMPLETED("Выполнен"),
	CANCELLED("Отменён");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}

}
