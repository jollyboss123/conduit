# Debezium Connector Configuration: Design Rationale

This document outlines the key configuration choices made for the Debezium PostgreSQL connector used in our CDC (Change Data Capture) pipeline, specifically tailored for the `outboxevent` table.

---

## Design Goals

1. **Filter out `DELETE` operations**  
   Outbox tables represent a stream of business events (e.g., `OrderCreated`, `OrderShipped`), which are only appended (inserted) or updated. Deletes are not part of the outbox pattern and should be dropped entirely.

2. **Prevent generation of tombstone messages**  
   The Outbox Kafka topics used are not log-compacted. Each event is uniquely identified (usually by a UUID or sequence ID). Tombstones are unnecessary and may introduce unwanted noise for consumers.

3. **Reroute Debezium’s default topic to a dedicated private/internal topic**  
   To decouple internal CDC event processing from public event consumption, raw outbox events are routed to a private topic (e.g., `orders.private.outboxevent`) using Debezium's `ByLogicalTableRouter` SMT.

4. **Normalize events into a public topic via a Kafka Streams application**  
   A downstream Kafka Streams service consumes from the private topic, transforms the data (e.g., enrich, filter, flatten), and publishes it to a versioned public topic (e.g., `orders.public.outboxevent.v1`).