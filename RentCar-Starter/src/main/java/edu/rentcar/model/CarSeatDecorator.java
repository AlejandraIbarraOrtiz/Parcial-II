package edu.rentcar.model;

public class CarSeatDecorator extends ReservationDecorator{

    private double price;

    public CarSeatDecorator(Reservation reservation, double price ){
        super(reservation);
        this.price = price;

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String description(){
        return reservation.description() + " + Carseat";
    }

    @Override
    public double calculatePrice(double rate){
        return reservation.calculatePrice(rate) + price;
    }
}
