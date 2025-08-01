package com.jolly.order_service.rest;

import com.jolly.order_service.model.OrderLineStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateOrderLineRequest {

  private OrderLineStatus newStatus;

}
