package service;

import model.Car;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarService {

    List<Car> cars = new ArrayList<>();

    public List<Car> getAllCars() {
        return cars;
    }

    public Car getCarById(Long id) {

        for (Car car : cars) {

            if (car.getId().equals(id)) {
                return car;
            }
        }

        return null;
    }

    public String addCar(Car car) {

        cars.add(car);

        return "Car added successfully";
    }


    public String updateCar(Long id, Car updatedCar) {

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


    public String deleteCar(Long id) {

        for (Car car : cars) {

            if (car.getId().equals(id)) {

                cars.remove(car);

                return "Car deleted successfully";
            }
        }

        return "Car not found";
    }
}