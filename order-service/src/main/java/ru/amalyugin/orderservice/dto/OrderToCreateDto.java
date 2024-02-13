package ru.amalyugin.orderservice.dto;

public record OrderToCreateDto(
        Long requestId,
        String description
) {
}
