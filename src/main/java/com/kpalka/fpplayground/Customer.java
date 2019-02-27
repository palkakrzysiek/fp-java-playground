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

    // Note that if you want to (de)serialize Optionals with Jackson you need to add jackson-datatype-jdk8 dependency and register Jdk8Module module in the Object mapper

    // Inspection info: Reports any uses of java.util.Optional<T>, java.util.OptionalDouble, java.util.OptionalInt, java.util.OptionalLong or com.google.common.base.Optional as the type for a field or a parameter. Optional was designed to provide a limited mechanism for library method return types where there needed to be a clear way to represent "no result". Using a field with type java.util.Optional is also problematic if the class needs to be Serializable, which java.util.Optional is not.
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
