package edu.rentcar.app;

import com.rentcar.paymentclient.PaymentApiClient;
import edu.rentcar.facade.RentCarFacade;
import edu.rentcar.repository.RentalModeRepository;
import edu.rentcar.repository.RentalRequestRepository;
import edu.rentcar.repository.ReservationRepository;
import edu.rentcar.repository.VehicleRepository;
import edu.rentcar.service.*;

/**
 * ApplicationServices es un Service Locator: los controllers consultan una clase global para obtener los servicios.
 */
public final class ApplicationServices {

    private static final VehicleService VEHICLE_SERVICE =
            new VehicleService(new VehicleRepository());

    private static final CustomerVerificationService CUSTOMER_VERIFICATION_SERVICE =
            new CustomerVerificationService();

    /*Creamos el servicio de pagos utilizando Proxy y Adapter.
    - El Proxy verifica primero si el cliente está autorizado.
    - Si está autorizado, el Proxy delega el pago al Adapter.
    - El Adapter se encarga de comunicarse con la API externa.
     */
    private static final PaymentService PAYMENT_SERVICE =
            new PaymentProxy(
                    new PaymentAdapter(
                           new PaymentApiClient("http://localhost:8080")
                    ),
                    CUSTOMER_VERIFICATION_SERVICE
            );

    private static final RentalRequestRepository RENTAL_REQUEST_REPOSITORY = new RentalRequestRepository();
    private static final RentalRequestService RENTAL_REQUEST_SERVICE = new RentalRequestService(RENTAL_REQUEST_REPOSITORY);

    private static final RentalModeRepository RENTAL_MODE_REPOSITORY = new RentalModeRepository();
    private static final RentalModeService RENTAL_MODE_SERVICE = new RentalModeService(RENTAL_MODE_REPOSITORY);

    // ReservationService requiere RentalRequestService y RentalModeService, por lo que debe instanciarse después
    private static final ReservationRepository RESERVATION_REPOSITORY = new ReservationRepository();
    private static final ReservationService RESERVATION_SERVICE =
            new ReservationService(
                RESERVATION_REPOSITORY,
                RENTAL_REQUEST_SERVICE,
                VEHICLE_SERVICE,
                CUSTOMER_VERIFICATION_SERVICE
            );

    private static final RentCarFacade RENT_CAR_FACADE =
            new RentCarFacade(
                    RESERVATION_SERVICE,
                    PAYMENT_SERVICE
            );

    private ApplicationServices() {
    }

    public static  VehicleService vehicleService(){
        return VEHICLE_SERVICE;
    }
    
    public static RentalRequestService rentalRequestService() {
        return RENTAL_REQUEST_SERVICE;
    }

    public static RentalModeService rentalModeService() {
        return RENTAL_MODE_SERVICE;
    }

    public static ReservationService reservationService() {
        return RESERVATION_SERVICE;
    }

    public static RentCarFacade rentCarFacade(){

        return RENT_CAR_FACADE;
    }
}
