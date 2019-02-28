package com.kpalka.fpplayground;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

class FailableBehaviourTest {

  io.vavr.collection.List<Customer> testCustomers = io.vavr.collection.List.of(
      ZonedDateTime.of(1970, 1, 1, 1, 0, 0, 0, UTC),
      ZonedDateTime.of(1990, 1, 1, 1, 0, 0, 0, UTC))
      .zipWithIndex()
      .map(bornOnIdx -> new Customer("Test John " + bornOnIdx._2,
          new Customer.Addrees(empty(), empty(), empty(), empty(), empty()),
          bornOnIdx._1,
          TRUE)
      );

  ZonedDateTime now =  ZonedDateTime.of(2020, 1, 1, 1, 0, 0, 0, UTC);
  Integer avgAge = 40;
  List<String> existingNames = List.of("Test John 0", "Test John 1");


    @Test
    void countAvgForExistingCustomers() {
      var cs = new FailableBehaviour.CustomerService(testCustomers);
      assertThat(FailableBehaviour.getAvgAge(cs, existingNames, now))
          .describedAs("Counts the avg of existing customers' age")
          .isEqualTo(avgAge);
    }
  }