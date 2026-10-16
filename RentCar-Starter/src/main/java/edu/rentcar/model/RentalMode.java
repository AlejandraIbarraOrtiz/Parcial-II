package edu.rentcar.model;

 //1. Esta interfaz es la abstracción principal
 // del patron factory que estamos utilizando para
 // crear las modalidades de alquiler

public interface RentalMode {
    Rate createRate();
    Mileage createMileage();
    MedicalPolicy createMedicalPolicy();
    RoadsideAssistance createRoadsideAssistance();

}
