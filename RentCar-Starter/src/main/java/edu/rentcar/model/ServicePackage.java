package edu.rentcar.model;

import java.util.ArrayList;
import java.util.List;

public class ServicePackage implements ServiceComponent{

    private String description;
    private List<ServiceComponent> components;

    public ServicePackage(String description) {

        this.description = description;
        this.components = new ArrayList<>();
    }

    public void addComponent(ServiceComponent component){

        components.add(component);
    }

    public void removeComponent(ServiceComponent component){

        components.remove(component);
    }

    @Override
    public String getDescription(){

        return description;
    }

    @Override
    public double getCost(){

        double total = 0;

        for (ServiceComponent component: components){

            total += component.getCost();
        }

        return total;

    }
}
