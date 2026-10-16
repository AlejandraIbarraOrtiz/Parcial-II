package edu.rentcar.model;

public class EconomyMode implements RentalMode{
    @Override
    public Rate createRate(){
        return new EconomyModeRate();
    }

    @Override
    public Mileage createMileage(){
        return new EconomyModeMileage();
    }

    @Override
    public MedicalPolicy createMedicalPolicy(){
        return new EconomyModeMedicalPolicy();
    }

    @Override
    public RoadsideAssistance createRoadsideAssistance(){
        return new EconomyModeRoadsideAssistance();
    }
}
