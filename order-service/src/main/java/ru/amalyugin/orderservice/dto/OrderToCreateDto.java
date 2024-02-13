package ru.amalyugin.orderservice.dto;

public record OrderToCreateDto(
        String username,
        Long requestId,
        String description,
        Long vendorId,
        Long orgId
) {
}
