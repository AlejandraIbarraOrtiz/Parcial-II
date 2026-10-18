package edu.rentcar.model;

public interface Reservation {
    String description();
    double calculatePrice(double rate);

    //Obtiene la solicitud de alquiler
    RentalRequest getRentalRequest();
}
