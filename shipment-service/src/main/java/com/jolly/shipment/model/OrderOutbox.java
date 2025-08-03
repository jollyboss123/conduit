package com.jolly.shipment.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@With
@Data
@Builder
@Jacksonized
public class OrderOutbox {
  private UUID id;
  private String aggregateType;
  private String aggregateId;
  private String type;
  private String payload;
  private Long timestamp;
  private String orderId;
  private String customerId;
}
