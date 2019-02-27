package com.kpalka.fpplayground;

import io.vavr.control.Option;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

class Zipping {

  private Zipping() {}

  @Value
  private static class ComparableAttribute {
    private String name;
    private Function<Customer, String> getter;
  }

  private static final List<ComparableAttribute> COMPARABLE_ATTRIBUTES = List.of(
      new ComparableAttribute("name", Customer::getName),
      new ComparableAttribute("address", c -> c.getAddress().toString()),
      new ComparableAttribute("born on", c -> c.getBornOn().toString()),
      new ComparableAttribute("is active", c -> c.getActive().toString())
  );

  private static Option<String> valueDiff(String valueName, String v1, String v2) {
    if (v1.equals(v2)) return none();
    else return some(valueName + ": " + v1 + " -> " + v2 + " | ");
  }

  static String customerDiff(Customer c1, Customer c2) {
    return COMPARABLE_ATTRIBUTES
        .stream()
        .reduce("",
            (String acc, ComparableAttribute ca) -> acc + valueDiff(
                ca.name,
                ca.getter.apply(c1),
                ca.getter.apply(c2)
            ).getOrElse(""),
            (String s1, String s2) -> s1 + " | " + s2 // makes sense for parallel processing, but here looks like too much overhead
        );
  }

  static String customerDiffWithVavr(Customer c1, Customer c2) {
    return io.vavr.collection.List.ofAll(COMPARABLE_ATTRIBUTES)
        .foldLeft("",
            (String acc, ComparableAttribute ca) -> acc + valueDiff(
                ca.name,
                ca.getter.apply(c1),
                ca.getter.apply(c2)
            ).getOrElse(""));
  }

  static List<String> compareSubsequentChangesStdLibsOnlyThroughAbomination(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var lastValue = new AtomicReference<>(customerStateSnapshots.get(0)); // ugh!
    // If one must mutate some state, than using good old for loops and variable doesn't look as awful as this
    return customerStateSnapshots
        .stream()
        .skip(1)
        .map(customer -> customerDiff(lastValue.getAndSet(customer), customer))
        .collect(Collectors.toList());
  }


  static List<String> compareSubsequentChanges(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var vavrList = io.vavr.collection.List.ofAll(customerStateSnapshots);
    return vavrList
        .zipWith(vavrList.drop(1), Zipping::customerDiffWithVavr)
        .asJava();
  }

  @Value
  private static class ComparisionState {
    private Customer lastVale;
    private io.vavr.collection.List<String> stateAcc;
  }

  static List<String> compareSubsequentChangesWithFoldLeft(List<Customer> customerStateSnapshots) {
    if (customerStateSnapshots.size() < 2) return Collections.emptyList();
    final var zero = new ComparisionState(customerStateSnapshots.get(0), io.vavr.collection.List.empty());
    final var vavrList = io.vavr.collection.List.ofAll(customerStateSnapshots);
    return vavrList
        .foldLeft(zero, (ComparisionState foldAcc, Customer c) ->
            // Such upgrading of accumulator wouldn't be possible had used a List<A> from the Java standard library. Its `add(A elem)` method doesn't return a new list object with the new element, but just a Boolean signaling if the collection has been mutated
            new ComparisionState(c, foldAcc.stateAcc.append(customerDiffWithVavr(foldAcc.lastVale, c))))
        .stateAcc
        .asJava();
  }


}
