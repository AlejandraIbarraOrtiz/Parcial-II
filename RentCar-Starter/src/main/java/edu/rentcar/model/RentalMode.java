package edu.rentcar.model;

public interface RentalMode {
    Rate createRate();
    Mileage createMileage();
    MedicalPolicy createMedicalPolicy();
    RoadsideAssistance createRoadsideAssistance();

}
