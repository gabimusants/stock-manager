package com.gabrielle.stockmanager.exception;

public class InvalidOrderStatusTransitionException extends RuntimeException {
  public InvalidOrderStatusTransitionException(String message) {
    super(message);
  }
}
