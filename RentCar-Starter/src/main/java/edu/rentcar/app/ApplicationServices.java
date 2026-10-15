package edu.rentcar.app;

import com.rentcar.paymentclient.PaymentApiClient;
import edu.rentcar.repository.VehicleRepository;
import edu.rentcar.service.PaymentAdapter;
import edu.rentcar.service.PaymentService;
import edu.rentcar.service.VehicleService;

/**
 * ApplicationServices es un Service Locator: los controllers consultan una clase global para obtener los servicios.
 */
public final class ApplicationServices {

    private static final VehicleService VEHICLE_SERVICE = new VehicleService(new VehicleRepository());

    private static final PaymentService PAYMENT_SERVICE = new PaymentAdapter(new PaymentApiClient("http://localhost:8080"));

    private ApplicationServices() {
    }

    public static VehicleService vehicleService() {
        return VEHICLE_SERVICE;
    }

    public static PaymentService paymentService(){

        return PAYMENT_SERVICE;
    }
}
