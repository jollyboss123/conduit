package com.jolly.kafka_streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class JsonSerde {

  public static <T> Serde<T> getValueSerde(Class<T> clazz) {
    JsonSerializer<T> serializer = new JsonSerializer<>();
    JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
    return Serdes.serdeFrom(serializer, deserializer);
  }
}