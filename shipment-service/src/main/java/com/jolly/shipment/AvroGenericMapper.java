package com.jolly.shipment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AvroGenericMapper {
  private static final AvroMapper AVRO_MAPPER = new AvroMapper();

  static {
    AVRO_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static <T> T deserialize(GenericRecord genericRecord, Class<T> clazz) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(genericRecord.getSchema());
      BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
      writer.write(genericRecord, encoder);
      encoder.flush();

      byte[] bytes = outputStream.toByteArray();
      return AVRO_MAPPER.readerFor(clazz)
          .with(new AvroSchema(genericRecord.getSchema())).readValue(bytes);
    }
  }
}

