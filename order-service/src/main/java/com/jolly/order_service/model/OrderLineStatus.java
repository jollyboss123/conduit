package com.jolly.order_service.model;

/**
 * Various statuses in which a {@link OrderLine} may be within.
 */
public enum OrderLineStatus {
  ENTERED,
  CANCELLED,
  SHIPPED
}
