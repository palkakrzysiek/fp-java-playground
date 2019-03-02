package com.kpalka.fpplayground;

import com.kpalka.fpplayground.Customer.Addrees;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ZippingTest {

  private Addrees address = Addrees
                .builder()
                .line1(of("Warszawska 1"))
                .line2(empty())
                .zipCode(of("00-000"))
                .city(of("Warsaw"))
                .country(of("Poland"))
                .build();

  private Customer c1 = Customer
            .builder()
            .name("Johny Kovalsky")
            .address(address)
            .active(TRUE)
            .bornOn(ZonedDateTime.of(2014, 3, 18, 12, 0, 0, 0, UTC))
            .build();

  private Customer c2 = Customer
      .builder()
      .name("John Kovalsky")
      .address(address)
      .active(TRUE)
      .bornOn(ZonedDateTime.of(2014, 3, 18, 12, 0, 0, 0, UTC))
      .build();

  private Customer c3 = Customer
            .builder()
            .name("Jan Kowalski")
            .address(address)
            .active(FALSE)
            .bornOn(ZonedDateTime.of(2019, 3, 18, 12, 0, 0, 0, UTC))
            .build();


  @Test
  void customerDiffTest() {
    assertThat(Zipping.customerDiff(c1, c3)).isEqualTo("name: Johny Kovalsky -> Jan Kowalski | born on: 2014-03-18T12:00Z -> 2019-03-18T12:00Z | is active: true -> false");
  }

  private List<Customer> customers = List.of(c1, c2, c3);

  private List<String> expectedChangesDescriptions = List.of(
      "name: Johny Kovalsky -> John Kovalsky",
      "name: John Kovalsky -> Jan Kowalski | born on: 2014-03-18T12:00Z -> 2019-03-18T12:00Z | is active: true -> false"
  );

  @Test
  void customerListDiffWithAtomicReference() {
    assertThat(Zipping.compareSubsequentChangesWithAtomicRefence(customers)).isEqualTo(expectedChangesDescriptions);
  }

  @Test
  void customerListDiffWithJavaStdLibTest() {
    assertThat(Zipping.compareSubsequentChanges(customers)).isEqualTo(expectedChangesDescriptions);
  }

  @Test
  void customerListDiffWithVavrTest() {
    assertThat(Zipping.compareSubsequentChangesWithAtomicRefence(customers)).isEqualTo(expectedChangesDescriptions);
  }

  @Test
  void customerListDiffWithVavrAndFoldLeftTest() {
    assertThat(Zipping.compareSubsequentChangesWithFoldLeft(customers)).isEqualTo(expectedChangesDescriptions);
  }

}