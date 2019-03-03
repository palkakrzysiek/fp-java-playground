package com.kpalka.fpplayground;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  ZonedDateTime now = ZonedDateTime.of(2020, 12, 31, 1, 0, 0, 0, UTC);
  Integer avgAge = 41;
  List<String> existingNames = List.of("Test John 0", "Test John 1");
  List<String> existingAndNonexistingNames = List.of("Test John 0", "Non-existing John");

  String errorProneCustomer = "Error-prone Customer";

  FailableBehaviour.CustomerService cs = new FailableBehaviour.CustomerService(testCustomers);

  @Test
  void countAvgForExistingCustomers() {
    assertThat(FailableBehaviour.getAvgAge(cs, existingNames, now))
        .describedAs("Counts the avg of existing customers' age")
        .isEqualTo(avgAge);
  }

  @Test
  void countAvgThrowing() {
    assertThatThrownBy(() -> FailableBehaviour.getAvgAge(cs, List.of(errorProneCustomer), now))
        .describedAs("Method can throw an expected error, but doesn't inform that it can fail in any way. Additionally, meaningful ServiceException is wrapped very generic RuntimeException")
        .isExactlyInstanceOf(RuntimeException.class);
  }

  @Test
  void countAvgForExistingCustomersWithTry() {
    assertThat(FailableBehaviour.getAvgAgeWithTry(cs, existingNames, now))
        .describedAs("Counts the avg of existing customers' age")
        .isEqualTo(Try.success(41));
  }

  @Test
  void countAvgForExistingCustomersWithTryWhenThereIsAServiceException() {
    var withErrorProneCustomer = io.vavr.collection.List.ofAll(existingNames).append(errorProneCustomer).asJava();
    assertThat(FailableBehaviour.getAvgAgeWithTry(cs, withErrorProneCustomer, now).getCause())
        .describedAs("Try.Failure has the cause of ServiceException")
        .isExactlyInstanceOf(FailableBehaviour.CustomerService.ServiceException.class);
  }
}