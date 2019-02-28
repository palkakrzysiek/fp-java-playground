package com.kpalka.fpplayground;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
class FailableBehaviour {

  @AllArgsConstructor
  static class CustomerService {
    private io.vavr.collection.List<Customer> customersSource;

    @EqualsAndHashCode
    static class ServiceException extends Exception {
      ServiceException(String msg) {
        super(msg);
      }
    }


    Optional<Customer> getByNameOptionalThrowing(String name) throws ServiceException {
      if ("Error-prone Customer".equals(name)) throw new ServiceException("Life is life... Nananana");
      return customersSource.find(c -> c.getName().equals(name)).toJavaOptional();
    }

    Try<Optional<Customer>> getByNameWithTry(String name) {
      return Try.of(() -> getByNameOptionalThrowing(name));
    }


  }

  private static Function<ZonedDateTime, Period> periodTo(ZonedDateTime to) {
    return from -> Period.between(from.toLocalDate(), to.toLocalDate());
  }

  @AllArgsConstructor
  static class AvgPeriodCounter {
    static final AvgPeriodCounter ZERO = new AvgPeriodCounter(Period.ZERO, 0);
    final Period sum;
    final Integer elementsNumber;
    AvgPeriodCounter plus(Period period) {
      return new AvgPeriodCounter(sum.plus(period), elementsNumber + 1);
    }
    AvgPeriodCounter plus(AvgPeriodCounter avgPeriodCounter) {
      return new AvgPeriodCounter(sum.plus(avgPeriodCounter.sum), elementsNumber + avgPeriodCounter.elementsNumber);
    }
    int getAvgYear(ZonedDateTime relativeTo) {
      return Period.between(relativeTo.minus(sum).toLocalDate(), relativeTo.toLocalDate()).getYears() / elementsNumber;
    }
  }

  static Integer getAvgAge(CustomerService cs, List<String> names, ZonedDateTime now) {
    // Don't do this at home
    Function<String, Optional<Customer>> aHackYouCanSometimesSpot = name -> {
      try {
        return cs.getByNameOptionalThrowing(name);
      } catch (CustomerService.ServiceException e) {
        // A service tries to inform me in the method signature that something can go wrong, but I cannot use a method that throws an exception inside `.map()`. But I REALLY want to use that fancy Stream feature... Hmm...
        throw new RuntimeException(e);
      }
    };

    var toAge = periodTo(now);

    // I often see similar examples used to show the possibilities of Stream<T> and method references...
    return names
        .stream()
        .map(aHackYouCanSometimesSpot) // ... and when I see such a call to service as a fragment of stream pipeline I smell something bad. Things can fail. In a nasty way. And I think such situations make some people, softly said, not very willing to incorporate the newer features of the language to their daily usage
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Customer::getBornOn)
        .map(toAge)
        .reduce(AvgPeriodCounter.ZERO,
            (AvgPeriodCounter acc, Period p) -> acc.plus(p),
            (AvgPeriodCounter acc1, AvgPeriodCounter acc2) -> acc1.plus(acc2)
        )
        .getAvgYear(now);
  }

  static Try<Integer> getAvgAgeWithTry(CustomerService cs, List<String> names, ZonedDateTime now) {

    var toAge = periodTo(now);

    return Try.traverse(names, cs::getByNameWithTry) // Try<Seq<Optional<Customer>>>
      .map(customers -> customers // Seq<Optional<Customer>>
          // If you're interested how pattern matching can look like see http://blog.vavr.io/integrating-partial-functions-into-javaslang/
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(Customer::getBornOn)
          .map(toAge)
          .foldLeft(AvgPeriodCounter.ZERO, AvgPeriodCounter::plus)
          .getAvgYear(now)
      )
        // and in the client of this method you can write something like...
        .onFailure(e -> log.warn("Cannot obtain customers {}", names, e))
        .onSuccess(result -> {if (log.isDebugEnabled()) {log.debug("For customers {} received the average age {}", names, result);}}) ;
  }
}
