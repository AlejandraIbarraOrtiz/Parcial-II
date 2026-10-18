package edu.rentcar.service;

import edu.rentcar.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalRequestTest {

    @Test
    @DisplayName("RF-001: Debería construir exitosamente una solicitud pasando los 7 datos obligatorios al Builder")
    void shouldCreateRentalRequestWithMandatoryFields() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 5);

        // Se pasan los 7 datos obligatorios directamente al constructor del Builder
        RentalRequest request = new RentalRequest.Builder(
                "REQ-001",
                "CUST-100",
                "VEH-500",
                startDate,
                endDate,
                "ExecutiveMode",
                "PENDING"
        ).build();

        assertNotNull(request);
        assertEquals("REQ-001", request.getId());
        assertEquals("CUST-100", request.getCustomerId());
        assertEquals("VEH-500", request.getVehicleId());
        assertEquals(startDate, request.getStartDate());
        assertEquals(endDate, request.getEndDate());
        assertEquals("ExecutiveMode", request.getRentalMode());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    @DisplayName("RF-001: Debería permitir configurar el atributo opcional isAditionalService")
    void shouldSetOptionalAditionalServiceField() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 5);

        RentalRequest request = new RentalRequest.Builder(
                "REQ-002",
                "CUST-101",
                "VEH-501",
                startDate,
                endDate,
                "EconomyMode",
                "APPROVED"
        )
                .isAitionalService(true)// Configuración del atributo opcional
                .build();

        assertNotNull(request);
        assertTrue(request.getAditionalService());
    }
}


