package ru.kolpakovee.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.kolpakovee.orderservice.dto.InventoryResponse;
import ru.kolpakovee.orderservice.dto.OrderLineItemDto;
import ru.kolpakovee.orderservice.dto.OrderRequest;
import ru.kolpakovee.orderservice.model.Order;
import ru.kolpakovee.orderservice.model.OrderLineItem;
import ru.kolpakovee.orderservice.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        var orderLineItemList = orderRequest.getOrderLineItemDTOs()
                .stream()
                .map(this::mapToOrderLineItem)
                .toList();

        order.setOrderLineItems(orderLineItemList);

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        // Call Inventory Service and place order if product is in stock
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponses != null;

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            return "Order placed successfully!";
        }
        else
            throw new IllegalArgumentException("Product is not in stock, please try again later!");
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}
