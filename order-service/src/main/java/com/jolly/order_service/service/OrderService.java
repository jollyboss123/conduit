package com.jolly.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolly.order_service.model.EntityNotFoundException;
import com.jolly.order_service.model.OrderLineStatus;
import com.jolly.order_service.model.PurchaseOrder;
import com.jolly.order_service.outbox.ExportedEvent;
import com.jolly.order_service.outbox.InvoiceCreatedEvent;
import com.jolly.order_service.outbox.OrderCreatedEvent;
import com.jolly.order_service.outbox.OrderLineUpdatedEvent;
import com.jolly.order_service.outbox.Outbox;
import com.jolly.order_service.outbox.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  OutboxRepository outboxRepository;

  /**
   * Add a new {@link PurchaseOrder}.
   *
   * @param order the purchase order
   * @return the persisted purchase order
   */
  @Transactional
  public PurchaseOrder addOrder(PurchaseOrder order) {
    order = this.purchaseOrderRepository.save(order);

    // Fire events for newly created PurchaseOrder
    final OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.of(order);
    this.outboxRepository.save(of(orderCreatedEvent));

    final InvoiceCreatedEvent invoiceCreatedEvent = InvoiceCreatedEvent.of(order);
    this.outboxRepository.save(of(invoiceCreatedEvent));

    return order;
  }

  /**
   * Update the a {@link PurchaseOrder} line's status.
   *
   * @param orderId     the purchase order id
   * @param orderLineId the purchase order line id
   * @param newStatus   the new order line status
   * @return the updated purchase order
   */
  @Transactional
  public PurchaseOrder updateOrderLine(long orderId, long orderLineId, OrderLineStatus newStatus) {
    Optional<PurchaseOrder> order = this.purchaseOrderRepository.findById(orderId);
    if (order.isEmpty()) {
      throw new EntityNotFoundException("Order with id " + orderId + " could not be found");
    }

    PurchaseOrder purchaseOrder = order.get();
    OrderLineStatus oldStatus = purchaseOrder.updateOrderLine(orderLineId, newStatus);
    this.purchaseOrderRepository.save(purchaseOrder);

    final OrderLineUpdatedEvent orderLineUpdated = OrderLineUpdatedEvent.of(orderId, orderLineId, newStatus, oldStatus);
    this.outboxRepository.save(of(orderLineUpdated));

    return purchaseOrder;
  }

  private static Outbox of(ExportedEvent exportedEvent) {
    Outbox outbox = new Outbox();
    outbox.setId(UUID.randomUUID());
    outbox.setAggregateId(exportedEvent.getAggregateId());
    outbox.setAggregateType(exportedEvent.getAggregateType());
    outbox.setType(exportedEvent.getType());
    outbox.setTimestamp(exportedEvent.getTimestamp());
    try {
      outbox.setPayload(mapper.writeValueAsString(exportedEvent.getPayload()));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return outbox;
  }
}
