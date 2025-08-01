package com.jolly.order_service.rest;

import com.jolly.order_service.model.OrderLine;
import com.jolly.order_service.model.PurchaseOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class CreateOrderRequest {

  private long customerId;
  private LocalDateTime orderDate;
  private List<OrderLineDto> lineItems;

  public CreateOrderRequest() {
    this.lineItems = new ArrayList<>();
  }

  public PurchaseOrder toOrder() {
    List<OrderLine> lines = lineItems.stream()
        .map(l -> new OrderLine(l.getItem(), l.getQuantity(), l.getTotalPrice()))
        .collect(Collectors.toList());

    return new PurchaseOrder(customerId, orderDate, lines);
  }
}
