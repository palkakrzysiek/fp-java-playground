package com.kpalka.fpplayground;

import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
class Customer {

  @Value
  @Builder(toBuilder = true)
  static class Addrees {
    private Optional<String> line1;
    private Optional<String> line2;
    private Optional<String> zipCode;
    private Optional<String> city;
    private Optional<String> country;
  }

  private String name;
  private Addrees address;
  private ZonedDateTime bornOn;
  private Boolean active;
}
