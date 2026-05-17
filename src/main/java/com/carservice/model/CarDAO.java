package com.carservice.model;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    public void insertCar(Car car) {
        String sql = "INSERT INTO cars (model, plate_number) VALUES (?, ?)";

        // try-with-resources: Bağlantı iş bitəndə avtomatik pool-a qayıdır
        HikariDataSource DatabaseConfig = null;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getModel());
            pstmt.setString(2, car.getPlateNumber());
            pstmt.executeUpdate();
            System.out.println("Avtomobil bazaya əlavə edildi!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        HikariDataSource DatabaseConfig = null;
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car();
                car.setId((long) rs.getInt("id"));
                car.setModel(rs.getString("com/carservice/model"));
                car.setPlateNumber(rs.getString("plate_number"));
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }
}