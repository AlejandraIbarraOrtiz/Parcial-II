package edu.rentcar.service;

import edu.rentcar.model.AditionalDriverDecorator;
import edu.rentcar.model.CarSeatDecorator;
import edu.rentcar.model.GPSDecorator;
import edu.rentcar.model.NormalReservation;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.RentalStore;
import edu.rentcar.model.Reservation;
import edu.rentcar.repository.ReservationRepository;

import java.util.List;
import java.util.function.Function;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RentalRequestService rentalRequestService;
    private final RentalModeService rentalModeService;

    public ReservationService(ReservationRepository reservationRepository,
                              RentalRequestService rentalRequestService,
                              RentalModeService rentalModeService) {
        this.reservationRepository = reservationRepository;
        this.rentalRequestService = rentalRequestService;
        this.rentalModeService = rentalModeService;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Crea una reserva y encadena dinámicamente los decoradores usando reduce.
     */
    public Reservation createReservation(String requestId, List<Function<Reservation, Reservation>> selectedDecorators) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental request ID is required.");
        }

        RentalRequest request = rentalRequestService.getAllRentalRequests().stream()
                .filter(req -> req.getId().equalsIgnoreCase(requestId.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rental request not found with ID: " + requestId));

        String reservationId = reservationRepository.generateNextId();
        Reservation baseReservation = new NormalReservation(reservationId, request);

        // Encadenamiento dinámico mediante el patrón Decorator
        Reservation decoratedReservation = selectedDecorators.stream()
                .reduce(baseReservation, (currentRes, decoratorFunc) -> decoratorFunc.apply(currentRes), (a, b) -> b);

        return reservationRepository.save(decoratedReservation);
    }

    public double calculateReservationTotal(Reservation reservation) {
        if (reservation == null) {
            return 0.0;
        }

        String modeName = extractModeName(reservation);
        double rate = rentalModeService.getAllRentalStores().stream()
                .filter(s -> s.getModeName().equalsIgnoreCase(modeName))
                .map(RentalStore::getConfiguredRate)
                .findFirst()
                .orElse(0.0);

        return reservation.calculatePrice(rate);
    }

    // Métodos fábrica paramétricos para recibir el precio dinámico desde el Controller/UI
    public Function<Reservation, Reservation> withGPS(double price) {
        return res -> new GPSDecorator(res, price);
    }

    public Function<Reservation, Reservation> withCarSeat(double price) {
        return res -> new CarSeatDecorator(res, price);
    }

    public Function<Reservation, Reservation> withAdditionalDriver(double price) {
        return res -> new AditionalDriverDecorator(res, price);
    }

    private String extractModeName(Reservation reservation) {
        if (reservation instanceof NormalReservation normal) {
            return normal.getRentalRequest().getRentalMode();
        }
        return "";
    }
}
