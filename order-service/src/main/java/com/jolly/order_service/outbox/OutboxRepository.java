package com.jolly.order_service.outbox;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboxRepository extends CrudRepository<Outbox, UUID> {

}
