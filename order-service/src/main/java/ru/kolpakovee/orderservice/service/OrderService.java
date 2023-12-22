package ru.kolpakovee.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.orderservice.dto.OrderLineItemDto;
import ru.kolpakovee.orderservice.dto.OrderRequest;
import ru.kolpakovee.orderservice.model.Order;
import ru.kolpakovee.orderservice.model.OrderLineItem;
import ru.kolpakovee.orderservice.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        var orderLineItemList = orderRequest.getOrderLineItemDTOs()
                .stream()
                .map(this::mapToOrderLineItem)
                .toList();

        order.setOrderLineItems(orderLineItemList);

        orderRepository.save(order);
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return null;
    }

}
