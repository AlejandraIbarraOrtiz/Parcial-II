package edu.rentcar.facade;

import edu.rentcar.model.IndividualService;
import edu.rentcar.model.ServicePackage;
import edu.rentcar.service.CustomerVerificationService;
import edu.rentcar.service.PaymentProxy;
import edu.rentcar.service.PaymentResult;
import edu.rentcar.service.PaymentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RentCarFacadeTest {

    @Test
    void shouldCreatePackageAndCalculateCost(){

        PaymentService paymentService = new PaymentService(){

            @Override
            public PaymentResult processPayment(String reservationId, String customerId, double amount, String paymentToken){

                return new PaymentResult(true, "Pago aprobado");
            }
        };

        RentCarFacade facade = new RentCarFacade(null, paymentService);

        ServicePackage servicePackage = facade.createPackage("Paquete básico");

        facade.addServiceToPackage(servicePackage, new IndividualService("GPS", 50000));

        facade.addServiceToPackage(servicePackage, new IndividualService("Silla para bebé", 30000));

        assertEquals(80000, facade.calculatePackageCost(servicePackage));
    }

    @Test
    void shouldProcessPayment(){

        PaymentService paymentService = new  PaymentService(){

            @Override
            public PaymentResult processPayment(String reservationId, String customerId, double amount, String PaymentToken){

                return new PaymentResult(true, "Pago aprobado");
            }
        };

        RentCarFacade facade = new RentCarFacade(null, paymentService);

        PaymentResult result = facade.processPayment("R001", "C001",150000,"token123");

        assertTrue(result.isApproved());
        assertEquals("Pago aprobado", result.getMessage());
    }

    @Test
    void shouldRejectUnauthorizedCustomer(){

        PaymentService paymentService = new PaymentService() {

            @Override
            public PaymentResult processPayment(String reservationId, String customerId, double amount, String paymentToken) {

                return new PaymentResult(true, "Payment approved");
            }
        };

        CustomerVerificationService verificationService = new CustomerVerificationService();

        PaymentService paymentProxy = new PaymentProxy(paymentService, verificationService);

        RentCarFacade facade = new RentCarFacade(null, paymentProxy);

        PaymentResult result = facade.processPayment("R001", "INVALID", 150000, "token123");

        assertFalse(result.isApproved());
        assertEquals("Cliente no autorizado", result.getMessage());
    }
}
