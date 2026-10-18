package edu.rentcar.repository;

import edu.rentcar.model.NormalReservation;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextIdSequence = 1001;

    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    public String generateNextId() {
        return "RES-" + (nextIdSequence++);
    }

    public Reservation save(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        reservations.add(reservation);
        return reservation;
    }

    public Optional<Reservation> findByRequestId(String requestId) {
        return reservations.stream()
                .filter(res -> extractRentalRequestId(res).equalsIgnoreCase(requestId))
                .findFirst();
    }

    public int count() {
        return reservations.size();
    }

    // Método auxiliar para extraer el ID de la solicitud desde la reserva (base o decorada)
    private String extractRentalRequestId(Reservation res) {
        Reservation current = res;
        while (current instanceof ReservationDecorator decorator) {
            // Desenvolver el decorador
            try {
                var field = ReservationDecorator.class.getDeclaredField("reservation");
                field.setAccessible(true);
                current = (Reservation) field.get(decorator);
            } catch (Exception e) {
                break;
            }
        }
        if (current instanceof NormalReservation normal) {
            return normal.getRentalRequest() != null ? normal.getRentalRequest().getId() : "";
        }
        return "";
    }
}