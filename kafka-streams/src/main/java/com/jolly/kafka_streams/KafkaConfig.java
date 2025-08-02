package com.jolly.kafka_streams;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.streams.KafkaStreamsMicrometerListener;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  KafkaStreamsConfiguration kStreamsConfig(@Value("${spring.profiles.active:default}") String activeProfile,
                                           @Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress,
                                           @Value("${kafka.schema.registry.url}") String schemaRegistryUrl,
                                           @Value("${spring.kafka.streams.replication-factor}") String replicationFactor,
                                           String offsetSetting) {
    Map<String, Object> props = new HashMap<>() {{
      put(APPLICATION_ID_CONFIG, "inventory." + activeProfile); // kafka stream uses it as prefix for internal topic and group.id for consumer
      put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
      put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
      put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getCanonicalName());
      put(TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE);
      put(StreamsConfig.APPLICATION_SERVER_CONFIG, String.format("%s:%s", "", "8080"));
      put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetSetting);
      put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getCanonicalName());
      put(StreamsConfig.REPLICATION_FACTOR_CONFIG, replicationFactor); // keep same as source topic
    }};

    return new KafkaStreamsConfiguration(props);
  }

  @Bean
  public String offsetSetting(@Value("${spring.profiles.active:default}") String activeProfile) {
    if ("staging".equalsIgnoreCase(activeProfile)) {
      return "latest";
    }

    return "earliest";
  }

  @Bean
  public KafkaStreamsMicrometerListener kafkaStreamsMicrometerListener(MeterRegistry meterRegistry) {
    return new KafkaStreamsMicrometerListener(meterRegistry);
  }

  @Bean
  public SchemaRegistryClient schemaRegistryClient(@Value("${kafka.schema.registry.url}") String schemaRegistryUrl) {
    return new CachedSchemaRegistryClient(schemaRegistryUrl, 40);
  }
}