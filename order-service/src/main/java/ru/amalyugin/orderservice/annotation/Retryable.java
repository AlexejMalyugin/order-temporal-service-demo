package ru.amalyugin.orderservice.annotation;

import io.temporal.workflow.SignalMethod;

public interface Retryable {

    @SignalMethod
    void retryNow();

}
