package edu.rentcar.model;

public class AditionalDriverDecorator extends ReservationDecorator{

    private double price;

    public AditionalDriverDecorator(Reservation reservation, double price ){
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
        return reservation.description() + " + Aditional draiver";
    }

    @Override
    public double calculatePrice(double rate){
        return reservation.calculatePrice(rate) + price;
    }
}
