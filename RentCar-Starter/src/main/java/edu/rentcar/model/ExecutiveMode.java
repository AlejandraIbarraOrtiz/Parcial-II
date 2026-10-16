package edu.rentcar.model;

public class ExecutiveMode implements RentalMode{
    @Override
    public Rate createRate() {
        return new ExecutiveModeRate();
    }

    @Override
    public Mileage createMileage() {
        return new ExecutiveModeMileage();
    }

    @Override
    public MedicalPolicy createMedicalPolicy() {
        return new ExecutiveModeMedicalPolicy();
    }

    @Override
    public RoadsideAssistance createRoadsideAssistance() {
        return new ExecutiveModeRoadsideAssistance();
    }
}
