package edu.rentcar.service;

import edu.rentcar.model.AditionalDriverDecorator;
import edu.rentcar.model.CarSeatDecorator;
import edu.rentcar.model.GPSDecorator;
import edu.rentcar.model.NormalReservation;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.RentalStore;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationDecorator;
import edu.rentcar.repository.ReservationRepository;

import java.lang.reflect.Field;
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
     * Crea una reserva y encadena dinámicamente los decoradores elegidos.
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

    /**
     * Obtiene la tarifa diaria (rate) desde RentalStore para la modalidad seleccionada
     * y ejecuta el cálculo recursivo delegando en la cadena del Decorator.
     */
    public double calculateReservationTotal(Reservation reservation) {
        if (reservation == null) {
            return 0.0;
        }

        // 1. Extrae el nombre de la modalidad desempaquetando los decoradores
        String modeName = extractModeName(reservation);

        // 2. Busca la tarifa diaria configurada en RentalStore para esa modalidad
        double baseRate = rentalModeService.getAllRentalStores().stream()
                .filter(s -> s.getModeName().equalsIgnoreCase(modeName))
                .map(RentalStore::getConfiguredRate)
                .findFirst()
                .orElse(0.0);

        // 3. Ejecuta el cálculo recursivo: (Días x Tarifa Base) + Servicios Adicionales
        return reservation.calculatePrice(baseRate);
    }

    // Métodos fábrica paramétricos para los servicios adicionales
    public Function<Reservation, Reservation> withGPS(double price) {
        return res -> new GPSDecorator(res, price);
    }

    public Function<Reservation, Reservation> withCarSeat(double price) {
        return res -> new CarSeatDecorator(res, price);
    }

    public Function<Reservation, Reservation> withAdditionalDriver(double price) {
        return res -> new AditionalDriverDecorator(res, price);
    }

    /**
     * Desempaqueta recursivamente la pila de decoradores hasta obtener la
     * NormalReservation y consultar su modalidad (RentalMode).
     **/
    private String extractModeName(Reservation reservation) {
        Reservation current = reservation;
        while (current instanceof ReservationDecorator decorator) {
            try {
                Field field = ReservationDecorator.class.getDeclaredField("reservation");
                field.setAccessible(true);
                current = (Reservation) field.get(decorator);
            } catch (Exception e) {
                break;
            }
        }

        if (current instanceof NormalReservation normal && normal.getRentalRequest() != null) {
            return normal.getRentalRequest().getRentalMode();
        }
        return "";
    }
}
