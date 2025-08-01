package com.jolly.order_service.rest;

import com.jolly.order_service.model.OrderLineStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderLineDto {

  private Long id;
  private String item;
  private int quantity;
  private BigDecimal totalPrice;
  private OrderLineStatus status;

  public OrderLineDto() {
  }

  public OrderLineDto(String item, int quantity, BigDecimal totalPrice) {
    this.item = item;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = OrderLineStatus.ENTERED;
  }

  public OrderLineDto(long id, String item, int quantity, BigDecimal totalPrice, OrderLineStatus status) {
    this.id = id;
    this.item = item;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = status;
  }

}

