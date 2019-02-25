package com.kpalka.dataclasses;

import io.vavr.control.Option;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@Builder(toBuilder = true)
class Consumer {

  @Value
  @Builder(toBuilder = true)
  static class Addrees {
    private Option<String> line1;
    private Option<String> line2;
    private Option<String> postalCode;
    private Option<String> city;
    private String country;
  }

  private String name;
  private Addrees address;
  private ZonedDateTime registeredOn;
  private Boolean active;
}
