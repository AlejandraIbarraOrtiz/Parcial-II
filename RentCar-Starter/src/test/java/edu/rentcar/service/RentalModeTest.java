package edu.rentcar.service;

import edu.rentcar.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RentalModeTest {

    @Test
    @DisplayName("RF-003: Abstract Factory debe instanciar la familia completa de productos para cada modalidad")
    void shouldCreateProductsFromAbstractFactory() {
        RentalMode economy = new EconomyMode();
        assertNotNull(economy.createRate());
        assertNotNull(economy.createMileage());
        assertNotNull(economy.createMedicalPolicy());
        assertNotNull(economy.createRoadsideAssistance());

        RentalMode executive = new ExecutiveMode();
        assertNotNull(executive.createRate());
        assertNotNull(executive.createMileage());

        RentalMode premium = new PremiumMode();
        assertNotNull(premium.createRate());
        assertNotNull(premium.createRoadsideAssistance());
    }

    @Test
    @DisplayName("RF-003: RentalStore debe recibir la fábrica abstracta y parametrizarse dinámicamente con setModes()")
    void shouldConfigureRentalStoreModesCorrectly() {
        RentalStore store = new RentalStore(new ExecutiveMode());
        store.setModes(200000.0, 300.0, "Póliza Ejecutiva $50k", "Asistencia 24/7 Coche Reemplazo");

        assertEquals("ExecutiveMode", store.getModeName());
        assertEquals(200000.0, store.getConfiguredRate());
        assertEquals(300.0, store.getConfiguredMileage());
        assertEquals("Póliza Ejecutiva $50k", store.getConfiguredPolicy());
        assertEquals("Asistencia 24/7 Coche Reemplazo", store.getConfiguredAssistance());
    }
}