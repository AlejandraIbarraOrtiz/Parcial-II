package edu.rentcar.facade;

import edu.rentcar.model.Reservation;
import edu.rentcar.model.ServiceComponent;
import edu.rentcar.model.ServicePackage;
import edu.rentcar.service.PaymentResult;
import edu.rentcar.service.PaymentService;
import edu.rentcar.service.ReservationService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RentCarFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public  RentCarFacade(ReservationService reservationService, PaymentService paymentService){

        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ServicePackage createPackage(String description){

        return new ServicePackage(description);
    }

    public void addServiceToPackage(ServicePackage servicePackage, ServiceComponent service){

        servicePackage.addComponent(service);
    }

    public double calculatePackageCost(ServicePackage servicePackage){

        return servicePackage.getCost();
    }

    /*
    - Crea una reserva y agrega los servicios adicionales seleccionados por el usuario.
    - La Fachada coordina esta operación y deja que ReservationService se encargue de crear la reserva
     */
    public Reservation createReservation(String requestId, boolean includeGPS, double gpsPrice, boolean includeCarSeat,
                                         double carSeatPrice, boolean includeAdditionalDriver, double driverPrice){

        List<Function<Reservation, Reservation>> selectedDecorators = new ArrayList<>();

        if (includeGPS){

            selectedDecorators.add(reservationService.withGPS(gpsPrice));
        }

        if (includeCarSeat){

            selectedDecorators.add(reservationService.withCarSeat(carSeatPrice));
        }

        if (includeAdditionalDriver){

            selectedDecorators.add(reservationService.withAdditionalDriver(driverPrice));
        }

        return reservationService.createReservation(requestId, selectedDecorators);
    }

    /*
    - Calcula el valor total de la reserva.
    - La Fachada delega el cálculo al ReservationService
     */
    public double calculatedReservationTotal(Reservation reservation){

        return reservationService.calculateReservationTotal(reservation);
    }

    public PaymentResult processPayment(String reservationId, String customerId,double amount, String paymentToken){

        return paymentService.processPayment(reservationId, customerId, amount, paymentToken);
    }
}
