package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import ru.amalyugin.orderservice.dto.OrderToCreateDto;

@ActivityInterface
public interface OrderDaoActivity {

    @ActivityMethod
    SaveOrderInputDto saveOrder(OrderToCreateDto dto);

    record SaveOrderInputDto(int count) {
    }

}
