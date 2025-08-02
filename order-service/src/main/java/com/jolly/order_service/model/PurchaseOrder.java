package com.jolly.order_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An entity mapping that represents a purchase order.
 */
@Setter
@Getter
@Entity
public class PurchaseOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_ids")
  @SequenceGenerator(name = "purchase_order_ids", sequenceName = "seq_purchase_order")
  private Long id;

  private long customerId;

  private LocalDateTime orderDate;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "purchaseOrder")
  private List<OrderLine> lineItems;

  protected PurchaseOrder() {
  }

  public PurchaseOrder(long customerId, LocalDateTime orderDate, List<OrderLine> lineItems) {
    this.customerId = customerId;
    this.orderDate = orderDate;
    this.lineItems = new ArrayList<>(lineItems);
    lineItems.forEach(line -> line.setPurchaseOrder(this));
  }

  public OrderLineStatus updateOrderLine(long orderLineId, OrderLineStatus newStatus) {
    for (OrderLine orderLine : lineItems) {
      if (orderLine.getId() == orderLineId) {
        OrderLineStatus oldStatus = orderLine.getStatus();
        orderLine.setStatus(newStatus);
        return oldStatus;
      }
    }

    throw new EntityNotFoundException("Order does not contain line with id " + orderLineId);
  }

  public BigDecimal getTotalValue() {
    return lineItems.stream().map(OrderLine::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}

