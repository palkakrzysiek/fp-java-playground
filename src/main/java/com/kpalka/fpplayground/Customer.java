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
    Optional<String> line1;
    Optional<String> line2;
    Optional<String> zipCode;
    Optional<String> city;
    Optional<String> country;
  }

  String name;
  Addrees address;
  ZonedDateTime bornOn;
  Boolean active;
}
