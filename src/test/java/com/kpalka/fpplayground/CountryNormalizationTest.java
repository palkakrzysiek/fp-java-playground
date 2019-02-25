package com.kpalka.fpplayground;

import org.junit.jupiter.api.Test;

import static com.kpalka.fpplayground.CountryNormalization.retrieveConsumers;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;

class CountryNormalizationTest {

  @Test
  public void deactivateConsumersTest() {
    CountryNormalization.deactivateUsers(retrieveConsumers()).forEach(customer ->
        assertThat(customer.getActive())
        .describedAs("Customer ["+customer+"] should not be active")
        .isEqualTo(FALSE)
      );
  }

}