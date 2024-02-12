package ru.amalyugin.orderservice.controller;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final WorkflowClient client;

    @GetMapping("/create")
    public ResponseEntity<String> createOrder() {
        OrderCreateWorkflow workflow = client.newWorkflowStub(
                OrderCreateWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("OrderCreateQueue")
                        .setWorkflowId("OrderCreate")
                        .build()
        );
        return ResponseEntity.ok(workflow.createOrder(new OrderToCreateDto(
                System.currentTimeMillis(),
                "Some Description"
        )));
    }

}
