package edu.rentcar.model;

public class PremiumMode implements RentalMode{
    @Override
    public Rate createRate() {
        return new PremiumModeRate();
    }

    @Override
    public Mileage createMileage() {
        return new PremiumModeMileage();
    }

    @Override
    public MedicalPolicy createMedicalPolicy() {
        return new PremiumModeMedicalPolicy();
    }

    @Override
    public RoadsideAssistance createRoadsideAssistance() {
        return new PremiumModeRoadsideAssistance();
    }
}
