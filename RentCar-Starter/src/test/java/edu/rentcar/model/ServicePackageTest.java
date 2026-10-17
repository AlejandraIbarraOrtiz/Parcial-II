package edu.rentcar.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServicePackageTest {

    @Test
    void packageshouldCalculateTotalCost(){

        IndividualService gps = new IndividualService("GPS", 50000);

        IndividualService babySeat = new IndividualService("Silla para bebé", 30000);

        ServicePackage servicePackage = new ServicePackage("Paquete básico");

        servicePackage.addComponent(gps);
        servicePackage.addComponent(babySeat);

        assertEquals(80000, servicePackage.getCost());
    }

    @Test
    void packageShouldContainAnotherPackage(){

        IndividualService gps = new IndividualService("GPS", 50000);

        IndividualService babySeat = new IndividualService("Silla para bebé", 30000);

        ServicePackage basicPackage = new ServicePackage("Paquete básico");

        basicPackage.addComponent(gps);
        basicPackage.addComponent(babySeat);

        ServicePackage premiumPackage = new ServicePackage("Paquete premium");

        premiumPackage.addComponent(basicPackage);

        assertEquals(80000, premiumPackage.getCost());
    }
}
