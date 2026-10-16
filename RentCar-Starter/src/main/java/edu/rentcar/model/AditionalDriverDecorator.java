package edu.rentcar.model;

public class AditionalDriverDecorator extends ReservationDecorator{

    public AditionalDriverDecorator(Reservation reservation){
        super(reservation);

    }

    @Override
    public String description(){
        return reservation.description() + "Aditional draiver";
    }

    @Override
    public double calculatePrice(){
        return reservation.calculatePrice() +
    }
}
