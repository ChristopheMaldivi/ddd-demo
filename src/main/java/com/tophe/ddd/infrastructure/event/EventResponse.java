package com.tophe.ddd.infrastructure.event;

import com.tophe.ddd.infrastructure.bus.BusResponse;
import io.vavr.control.Try;

public class EventResponse<V> extends BusResponse<V, Try<V>> {
  public EventResponse(Try<V> t) {
    super(t);
  }
}