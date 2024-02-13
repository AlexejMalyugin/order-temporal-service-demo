package ru.amalyugin.orderservice.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import ru.amalyugin.orderservice.dto.OrderToCreateDto;

@WorkflowInterface
public interface OrderCreateWorkflow {

    @WorkflowMethod(name = "create-order-workflow")
    String createOrder(OrderToCreateDto dto);

}
