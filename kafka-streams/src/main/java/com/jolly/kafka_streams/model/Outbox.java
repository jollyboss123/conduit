package com.jolly.kafka_streams.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.UUID;

//@Value
@With
@Builder
@Data
@Jacksonized
public class Outbox {
  UUID id;
  @JsonProperty("aggregate_type")
  String aggregateType;
  @JsonProperty("aggregate_id")
  String aggregateId;
  String type;
  String payload;
  Long timestamp;

  @Value
  @Builder
  @Jacksonized
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Payload {
    String orderId;
    String customerId;
  }
}
