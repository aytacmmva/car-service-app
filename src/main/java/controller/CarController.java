package controller;

import model.Car;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    List<Car> cars = new ArrayList<>();

    @GetMapping
    public List<Car> getAllCars() {
        return cars;
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {

        for (Car car : cars) {
            if (car.getId().equals(id)) {
                return car;
            }
        }

        return null;
    }

    @PostMapping
    public String addCar(@RequestBody Car car) {

        cars.add(car);

        return "Car added successfully";
    }


    @PutMapping("/{id}")
    public String updateCar(@PathVariable Long id,
                            @RequestBody Car updatedCar) {

        for (Car car : cars) {

            if (car.getId().equals(id)) {

                car.setPlateNumber(updatedCar.getPlateNumber());
                car.setModel(updatedCar.getModel());
                car.setLastServiceDate(updatedCar.getLastServiceDate());
                car.setYear(updatedCar.getYear());

                return "Car updated successfully";
            }
        }


        return "Car not found";
    }

    @DeleteMapping("/{id}")
    public String deleteCar(@PathVariable Long id) {

        for (Car car : cars) {

            if (car.getId().equals(id)) {

                cars.remove(car);

                return "Car deleted successfully";
            }
        }

        return "Car not found";
    }
}