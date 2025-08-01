package com.jolly.order_service.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolly.order_service.model.OrderLineStatus;

import java.time.Instant;

/**
 * An 'Order' event that indicates an order line's status has changed.
 */
public class OrderLineUpdatedEvent implements ExportedEvent {

  private static final ObjectMapper mapper = new ObjectMapper();

  private final long orderId;
  private final long orderLineId;
  private final OrderLineStatus newStatus;
  private final OrderLineStatus oldStatus;
  private final Instant timestamp;

  public OrderLineUpdatedEvent(long orderId, long orderLineId, OrderLineStatus newStatus, OrderLineStatus oldStatus) {
    this.orderId = orderId;
    this.orderLineId = orderLineId;
    this.newStatus = newStatus;
    this.oldStatus = oldStatus;
    this.timestamp = Instant.now();
  }

  public static OrderLineUpdatedEvent of(long orderId, long orderLineId, OrderLineStatus newStatus, OrderLineStatus oldStatus) {
    return new OrderLineUpdatedEvent(orderId, orderLineId, newStatus, oldStatus);
  }

  @Override
  public String getAggregateId() {
    return String.valueOf(orderId);
  }

  @Override
  public String getAggregateType() {
    return "Order";
  }

  @Override
  public JsonNode getPayload() {
    return mapper.createObjectNode()
        .put("orderId", orderId)
        .put("orderLineId", orderLineId)
        .put("oldStatus", oldStatus.name())
        .put("newStatus", newStatus.name());
  }

  @Override
  public String getType() {
    return "OrderLineUpdated";
  }

  @Override
  public Instant getTimestamp() {
    return timestamp;
  }
}
