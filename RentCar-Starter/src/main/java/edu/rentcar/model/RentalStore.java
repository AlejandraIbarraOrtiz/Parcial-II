package edu.rentcar.model;

public class RentalStore {
    private final RentalMode mode;
    private final Rate rate;
    private final Mileage mileage;
    private final MedicalPolicy medicalPolicy;
    private final RoadsideAssistance roadsideAssistance;

    private double configuredRate;
    private double configuredMileage;
    private String configuredPolicy;
    private String configuredAssistance;

    public RentalStore(RentalMode mode) {
        this.mode = mode;
        this.rate = mode.createRate();
        this.mileage = mode.createMileage();
        this.medicalPolicy = mode.createMedicalPolicy();
        this.roadsideAssistance = mode.createRoadsideAssistance();
    }

    public void setModes(double rate1, double mileage1, String policy, String asistance) {
        // Ejecución de los métodos exigidos en las interfaces
        this.configuredRate = rate.setRate(rate1);
        this.configuredMileage = mileage.setMileage(mileage1);
        this.configuredPolicy = medicalPolicy.setMedicalpolicy(policy);
        this.configuredAssistance = roadsideAssistance.setRoadsideAssistance(asistance);
    }

    public String getModeName() {
        return mode.getClass().getSimpleName();
    }

    public double getConfiguredRate() {
        return configuredRate;
    }

    public double getConfiguredMileage() {
        return configuredMileage;
    }

    public String getConfiguredPolicy() {
        return configuredPolicy;
    }

    public String getConfiguredAssistance() {
        return configuredAssistance;
    }
}