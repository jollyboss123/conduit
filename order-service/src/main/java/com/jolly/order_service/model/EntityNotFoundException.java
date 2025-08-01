package com.jolly.order_service.model;

import java.io.Serial;

/**
 * An exception that indicates an entity could not be found.
 */
public class EntityNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -1L;

  public EntityNotFoundException(String message) {
    super(message);
  }
}
