package edu.rentcar.service;

public class PaymentResult {

    private final boolean approved;
    private final String message;

    public PaymentResult(boolean approved, String message){

        this.approved = approved;
        this.message = message;
    }

    public boolean isApproved(){

        return  approved;
    }

    public String getMessage(){

        return message;
    }
}
