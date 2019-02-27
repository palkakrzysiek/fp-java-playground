package com.kpalka.fpplayground;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.kpalka.fpplayground.CountryNormalization.deactivateCustomers;
import static com.kpalka.fpplayground.CountryNormalization.normalizeCountry;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

class CountryNormalizationTest {

  List<Customer> getTestConsumers() {
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
            .bornOn(ZonedDateTime.of(2014, 3, 18, 12, 0, 0, 0, UTC))
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
            .active(FALSE)
            .bornOn(ZonedDateTime.of(2019, 3, 18, 12, 0, 0, 0, UTC))
            .build()
    );
  }



  @Test
  void deactivateConsumersTest() {
    deactivateCustomers(getTestConsumers()).forEach(customer ->
        assertThat(customer.getActive())
            .describedAs("Customer [" + customer + "] should not be active")
            .isEqualTo(FALSE)
    );
  }

  Set<String> ALlOWED_COUNTRY_NAMES = Set.of("USA", "France", "India", "Poland");

  @Test
  void countryNamesAfterNormalizationContainOnlyAllowedValues() {
    normalizeCountry(getTestConsumers()) .forEach(customer ->
        customer.getAddress().getCountry().ifPresent(countryName ->
            assertThat(countryName)
            .describedAs("The country name of [" + customer + "] after the normalization, if present should be within allowed value set ["+ ALlOWED_COUNTRY_NAMES + "]")
            .isIn(ALlOWED_COUNTRY_NAMES)
            )
        );
  }

}