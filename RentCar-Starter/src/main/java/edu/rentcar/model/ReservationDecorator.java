package edu.rentcar.model;

public abstract class ReservationDecorator implements Reservation {
    protected Reservation reservation;

    public ReservationDecorator(Reservation reservation){
        this.reservation = reservation;

    }
    @Override
    public String description() {
        return reservation.description();
    }

    @Override
    public double calculatePrice(double rate) {
        return reservation.calculatePrice(rate);
    }
}
