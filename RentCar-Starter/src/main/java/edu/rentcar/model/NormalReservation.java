package edu.rentcar.model;

import java.time.temporal.ChronoUnit;

public class NormalReservation implements Reservation {
    private String id;
    private RentalRequest rentalRequest;

    public NormalReservation(String id, RentalRequest rentalRequest) {
        this.id = id;
        this.rentalRequest = rentalRequest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RentalRequest getRentalRequest() {
        return rentalRequest;
    }

    public void setRentalRequest(RentalRequest rentalRequest) {
        this.rentalRequest = rentalRequest;
    }

    @Override
    public String description() {
        return "Base Reservation (" + rentalRequest.getRentalMode() + ")";
    }

    @Override
    public double calculatePrice(double rate) {
        if (rentalRequest == null || rentalRequest.getStartDate() == null || rentalRequest.getEndDate() == null) {
            return rate;
        }

        long days = ChronoUnit.DAYS.between(rentalRequest.getStartDate(), rentalRequest.getEndDate());
        if (days <= 0) {
            days = 1; // Mínimo 1 día de alquiler
        }

        return days * rate;
    }
}
