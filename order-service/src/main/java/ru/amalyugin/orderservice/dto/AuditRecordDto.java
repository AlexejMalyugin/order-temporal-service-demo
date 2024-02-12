package ru.amalyugin.orderservice.dto;

public record AuditRecordDto(
        String id,
        Long requestId,
        AuditStatus status

) {
    public enum AuditStatus {
        SUCCESS,
        ERROR
    }
}
