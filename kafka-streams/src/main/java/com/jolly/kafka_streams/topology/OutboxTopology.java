package com.jolly.kafka_streams.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jolly.kafka_streams.JsonSerde;
import com.jolly.kafka_streams.SerdeUtil;
import com.jolly.kafka_streams.avro.NormalizedOutbox;
import com.jolly.kafka_streams.model.Outbox;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Profile("outbox")
@Slf4j
public class OutboxTopology {
  private final Serde<Outbox> outboxSerde;
  private final Serde<NormalizedOutbox> normalizedOutboxSerde;
  private static final ObjectMapper mapper = new ObjectMapper();
  private final ObjectReader payloadReader = mapper.reader().forType(Outbox.Payload.class);

  public OutboxTopology(SchemaRegistryClient schemaRegistryClient,
                        @Value("${kafka.schema.registry.url}") String schemaRegistryUrl) {
    this.outboxSerde = JsonSerde.getValueSerde(Outbox.class);
    this.normalizedOutboxSerde = SerdeUtil.getValueSerde(schemaRegistryClient, schemaRegistryUrl);
  }

  @Autowired
  public void buildTopology(StreamsBuilder streamsBuilder) {
    streamsBuilder.stream("orders.private.outbox", Consumed.with(Serdes.String(), outboxSerde))
        .map((key, value) -> {
          Outbox.Payload payload = null;
          try {
            payload = payloadReader.readValue(value.getPayload());
          } catch (IOException e) {
            log.warn("Failed to deserialize payload: ", e);
          }

          return new KeyValue<>(key, NormalizedOutbox.newBuilder()
              .setId(value.getId())
              .setAggregateType(value.getAggregateType())
              .setAggregateId(value.getAggregateId())
              .setType(value.getType())
              .setPayload(value.getPayload())
              .setTimestamp(value.getTimestamp())
              .setOrderId(Optional.ofNullable(payload)
                  .map(Outbox.Payload::getOrderId)
                  .orElse(null))
              .setCustomerId(Optional.ofNullable(payload)
                  .map(Outbox.Payload::getCustomerId)
                  .orElse(null))
              .build());
        })
        .to("orders.public.outbox.v1", Produced.with(Serdes.String(), normalizedOutboxSerde));
  }
}
