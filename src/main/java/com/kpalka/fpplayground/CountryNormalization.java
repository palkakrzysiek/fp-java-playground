package com.kpalka.fpplayground;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class CountryNormalization {
  static List<Customer> retrieveConsumers() {
    return List.of(
        Customer
            .builder()
            .name("John Kovalsky")
            .address(Customer.Addrees
                .builder()
                .line1(of("Warszawska 1"))
                .line2(empty())
                .zipCode(of("00-000"))
                .city(of("Warsaw"))
                .country(of("Poland"))
                .build())
            .active(TRUE)
            .registeredOn(ZonedDateTime.of(2014, 3, 18, 12, 0, 0, 0, UTC))
            .build(),
        Customer
            .builder()
            .name("Jan Kowalski")
            .address(Customer.Addrees
                .builder()
                .line1(of("Warszawska 2"))
                .line2(empty())
                .zipCode(of("00-001"))
                .city(of("Warszawa"))
                .country(of("Polska"))
                .build())
            .active(TRUE)
            .registeredOn(ZonedDateTime.of(2019, 3, 18, 12, 0, 0, 0, UTC))
            .build()
    );
  }

  static List<Customer> deactivateUsers(List<Customer> customers) {
    return customers
        .stream()
        .map(customer -> customer
            .toBuilder()
            .active(FALSE)
            .build())
        .collect(Collectors.toList());
  }

  static List<Customer> normalizeCountry(List<Customer> customers, String oldVal, String newVal) {
    return customers
        .stream()
        .map(customer -> {
          final var oldAddress = customer.getAddress();
          return customer
              .toBuilder()
              .address(oldAddress
                  .toBuilder()
                  .country(oldAddress.getCountry()
                      .map(countryName -> countryName.replace(oldVal, newVal)))
                  .build())
              .build();
        }).collect(Collectors.toList());
  }
}
