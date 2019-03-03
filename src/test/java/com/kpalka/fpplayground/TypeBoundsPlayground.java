package com.kpalka.fpplayground;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

class TypeBoundsPlayground {

  interface RawMaterial {}
  interface Steel extends RawMaterial {}
  interface Vehicle {}
  interface Car extends Vehicle {}

  RawMaterial rawMaterialObject = new RawMaterial() { };
  Steel steelObject = new Steel() { };
  Vehicle vehicleObject = new Vehicle() { };
  Car carObject = new Car() { };

  Function<Steel, Vehicle> steelToVehicle = steel -> vehicleObject;
  Function<Steel, Car> steelToCar = steel -> carObject;
  Function<RawMaterial, Car> rawMaterialToCar = rawMaterial -> carObject;
  Function<RawMaterial, Vehicle> rawMaterialToVehicle = rawMaterial -> vehicleObject;

  @Test
  void typeBoundsTest() {

    // Optional<Vehicle> vehicle = Optional.of(steelObject).map((Function<? super Steel, ? extends Vehicle>) mapper);

    Optional<Vehicle> vehicle1 = Optional.of(steelObject).map(steelToVehicle);
    Optional<Vehicle> vehicle2 = Optional.of(steelObject).map(steelToCar);
    Optional<Vehicle> vehicle3 = Optional.of(steelObject).map(rawMaterialToCar);
    Optional<Vehicle> vehicle4 = Optional.of(steelObject).map(rawMaterialToVehicle);

    // no assertions, just checking if the code compiles
  }


}
