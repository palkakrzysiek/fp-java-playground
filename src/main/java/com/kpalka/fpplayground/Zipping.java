package com.kpalka.fpplayground;

import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

class Zipping {

  private Zipping() {
  }

  @Value
  private static final class ComparableAttribute {
    final String name;
    final Function<Customer, String> getter;
  }

  private static final List<ComparableAttribute> COMPARABLE_ATTRIBUTES = List.of(
      new ComparableAttribute("name", Customer::getName),
      new ComparableAttribute("address", c -> c.getAddress().toString()),
      new ComparableAttribute("born on", c -> c.getBornOn().toString()),
      new ComparableAttribute("is active", c -> c.getActive().toString())
  );

  private static Optional<String> valueDiff(String valueName, String v1, String v2) {
    if (v1.equals(v2)) return Optional.empty();
    else return Optional.of(valueName + ": " + v1 + " -> " + v2);
  }

  static String customerDiff(Customer c1, Customer c2) {
    return COMPARABLE_ATTRIBUTES
        .stream()
        .map(attr -> valueDiff(attr.name, attr.getGetter().apply(c1), attr.getter.apply(c2)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.joining(" | "));
  }

  static List<String> compareSubsequentChangesWithAtomicReference(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var lastValue = new AtomicReference<>(customerStateSnapshots.get(0)); // ugh!
    // If one must mutate some state, than using good old for loops and variable doesn't look as awful as this
    return customerStateSnapshots
        .stream()
        .skip(1)
        .map(customer -> customerDiff(lastValue.getAndSet(customer), customer))
        .collect(Collectors.toList());
  }

  static List<String> compareSubsequentChangesWithVavr(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var vavrList = io.vavr.collection.List.ofAll(customerStateSnapshots);
    return vavrList
        .zipWith(vavrList.drop(1), Zipping::customerDiff)
        .asJava();
  }

  @Value
  private static final class ComparisionState {
    final Customer lastVale;
    final io.vavr.collection.List<String> stateAcc;
  }

  static List<String> compareSubsequentChangesWithFoldLeft(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var zero = new ComparisionState(customerStateSnapshots.get(0), io.vavr.collection.List.empty());
    final var vavrList = io.vavr.collection.List.ofAll(customerStateSnapshots);
    return vavrList
        .drop(1)
        // foldLeft requires the zero element because it's the default one if the list is empty. a data type like NonEmptyList could have a foldLeft implementation which doesn't require the zero value as there will be always at least one element. Unfortunately the proposal to add it (https://github.com/vavr-io/vavr/issues/1244) might have been not too-well motivated and has been rejected
        .foldLeft(zero, (ComparisionState foldAcc, Customer c) ->
            // Such upgrading of accumulator wouldn't be possible had used a List<A> from the Java standard library. Its `add(A elem)` method doesn't return a new list object with the new element, but just a Boolean signaling if the collection has been mutated
            new ComparisionState(c, foldAcc.stateAcc.append(customerDiff(foldAcc.lastVale, c))))
        .stateAcc
        .asJava();
  }


}
