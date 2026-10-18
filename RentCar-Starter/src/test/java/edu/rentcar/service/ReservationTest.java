package edu.rentcar.service;

import edu.rentcar.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private RentalRequest request;
    private final double dailyRate = 120000.0; // Tarifa diaria de $120.000 COP

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 4); // 3 días de alquiler

        // 7 datos obligatorios requeridos en el constructor de RentalRequest.Builder
        request = new RentalRequest.Builder(
                "REQ-500",
                "CUST-001",
                "VEH-001",
                startDate,
                endDate,
                "EconomyMode",
                "CONFIRMED"
        )
                .isAitionalService(true)
                .build();
    }

    @Test
    @DisplayName("RF-002: NormalReservation debe calcular el costo base multiplicando los días por la tarifa de la modalidad")
    void shouldCalculateBaseReservationPriceBasedOnDays() {
        Reservation reservation = new NormalReservation("RES-100", request);

        // 3 días * $120.000 = $360.000 COP
        double totalPrice = reservation.calculatePrice(dailyRate);

        assertEquals(360000.0, totalPrice);
        assertTrue(reservation.description().contains("EconomyMode"));
    }

    @Test
    @DisplayName("RF-005: El patrón Decorator debe sumar el costo y la descripción de múltiples servicios adicionales de forma recursiva")
    void shouldCalculatePriceRecursivelyWithDecorators() {
        Reservation baseReservation = new NormalReservation("RES-101", request);

        // Encadenamiento del patrón Decorator
        Reservation withGps = new GPSDecorator(baseReservation, 25000.0);
        Reservation withCarSeat = new CarSeatDecorator(withGps, 18000.0);
        Reservation fullReservation = new AditionalDriverDecorator(withCarSeat, 35000.0);

        // Cálculo esperado: (3 días * $120.000) + $25.000 + $18.000 + $35.000 = $438.000 COP
        double expectedTotal = 360000.0 + 25000.0 + 18000.0 + 35000.0;

        assertEquals(expectedTotal, fullReservation.calculatePrice(dailyRate));
        assertTrue(fullReservation.description().contains("GPS"));
        assertTrue(fullReservation.description().contains("Carseat"));
        assertTrue(fullReservation.description().contains("Aditional draiver"));
    }
}