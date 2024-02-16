package ru.amalyugin.orderservice.activity.impl;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.OrderDaoActivity;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;

import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;

@Slf4j
@Component
@ActivityImpl(taskQueues = CREATE_ORDER_QUEUE_NAME)
public class OrderDaoActivityImpl implements OrderDaoActivity {

    @Override
    public SaveOrderInputDto saveOrder(OrderToCreateDto dto) {
        log.info("Order saved: {}", dto);
        if (System.currentTimeMillis() % 2 == 0) {
            throw new RuntimeException("Save failed: " + dto);
        } else {
            return new SaveOrderInputDto(1);
        }
    }

}
