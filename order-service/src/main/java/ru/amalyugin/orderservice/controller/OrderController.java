package ru.amalyugin.orderservice.controller;

import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

import java.util.UUID;

import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;
import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_WF_ID_PREFIX;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final WorkflowClient client;

    @GetMapping("/create")
    public ResponseEntity<String> createOrder() {
        String id = UUID.randomUUID().toString();

        OrderCreateWorkflow workflow = client.newWorkflowStub(
                OrderCreateWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(CREATE_ORDER_QUEUE_NAME)
                        .setWorkflowIdReusePolicy(
                                WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE
                        )
                        .setWorkflowId(CREATE_ORDER_WF_ID_PREFIX + id)
                        .build()
        );
        return ResponseEntity.ok(workflow.createOrder(new OrderToCreateDto(
                "USERNAME",
                System.currentTimeMillis(),
                "Some Description",
                143542L,
                121L
        )));
    }

}
