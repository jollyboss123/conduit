package com.jolly.shipment.consumer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolly.shipment.AvroGenericMapper;
import com.jolly.shipment.model.OrderOutbox;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


@Component
@Slf4j
public class ShipmentConsumer {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @KafkaListener(topics = "${kafka.topic}")
  public void receive(@Payload ConsumerRecord<String, GenericRecord> data) throws IOException {
    GenericRecord event = data.value();
    OrderOutbox orderOutbox = AvroGenericMapper.deserialize(event, OrderOutbox.class);
    log.info(String.valueOf(data.value()));
    JsonNode payload = OBJECT_MAPPER.readValue(orderOutbox.getPayload(), JsonNode.class);

    log.info("customer Id: {}", orderOutbox.getCustomerId());

    Optional.ofNullable(payload.get("lineItems"))
        .map(lineItems -> {
          lineItems.elements().forEachRemaining(node -> {
            log.info("{} {}", node.get("item").asText(), node.get("quantity").asText());
          });
          return null;
        });

  }

}
