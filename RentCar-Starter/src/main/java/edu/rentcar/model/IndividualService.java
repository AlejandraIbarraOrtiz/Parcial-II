package edu.rentcar.model;

public class IndividualService implements ServiceComponent{

    private String description;
    private double cost;

    public IndividualService(String description, double cost) {

        this.description = description;
        this.cost = cost;
    }

    @Override
    public String getDescription(){

        return description;
    }

    @Override
    public double getCost(){

        return cost;
    }
}
