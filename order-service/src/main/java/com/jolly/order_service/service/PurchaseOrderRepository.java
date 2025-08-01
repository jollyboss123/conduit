package com.jolly.order_service.service;

import com.jolly.order_service.model.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long> {

}
