package edu.rentcar.model;

public class NormalReservation implements Reservation{
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
        return "";
    }

    @Override
    public double calculatePrice() {
        return 0;
    }
}
