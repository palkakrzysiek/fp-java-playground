package com.kpalka.fpplayground;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

@Slf4j
class CountryNormalization {

  static List<Customer> deactivateCustomers(List<Customer> customers) {
    return customers
        .stream()
        .map(customer -> customer
            .toBuilder()
            .active(FALSE)
            .build())
        .collect(Collectors.toList());
  }

  @FunctionalInterface
  private interface CustomerMapper extends Function<Customer, Customer> {
  }

  private static CustomerMapper countryRenamer(String oldValue, String newValue) {
    return customer -> {
      var oldAddress = customer.getAddress();
      return customer
          .toBuilder()
          .address(oldAddress
              .toBuilder()
              .country(oldAddress
                  .getCountry()
                  // don't revert to isPresent which makes the code looks not better than if (x != null) {...}
                  // remember that Optional<A> has convenient .map(A -> B) and .flatMap(A -> Optional<B>) methods
                  .map(countryName -> countryName.replace(oldValue, newValue)))
              .build())
          .build();
    };
  }

  static List<Customer> normalizeCountry(List<Customer> customers) {
    var oldVal = "Polska";
    var newVal = "Poland";
    var result = customers
        .stream()
        // as customer is immutable we dont have to worry about changes to the original values in theRenamer, whatever its implementation is...
        .map(countryRenamer(oldVal, newVal))
        .collect(Collectors.toList());
    // ... and can compare the original list to the new list
    if (log.isDebugEnabled()) log.debug("Normalized countries from {} resulting in {}", customers, result);
    return result;
  }
}
