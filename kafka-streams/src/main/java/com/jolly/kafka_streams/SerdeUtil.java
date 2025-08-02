package com.jolly.kafka_streams;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;

import java.util.Map;

public class SerdeUtil {

  public static <T extends SpecificRecord> SpecificAvroSerde<T> getValueSerde(SchemaRegistryClient client, String schemaRegistryUrl) {
    final SpecificAvroSerde<T> valueSerde = new SpecificAvroSerde<>(client);

    valueSerde.configure(
        Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl),
        false);

    return valueSerde;
  }
}
