package edu.rentcar.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProxyTest {

    // Verifica que un cliente no autorizado no pueda procesar el pago.
    @Test
    void unauthorizedCustomerShouldNotProcessPayment(){

        PaymentService paymentService = new PaymentService(){

            @Override
            public PaymentResult processPayment(String reservationId, String customerId, double amount,
                                                String paymentToken){

                fail("No se debe llamar al servicio de pago");
                return null;
            }
        };

        CustomerVerificationService verificationService = new CustomerVerificationService();

        PaymentProxy paymentProxy = new PaymentProxy(paymentService, verificationService);

        PaymentResult result = paymentProxy.processPayment("RES-1001",
                "CUS-1003", 350000,"tok_approved");
        
        assertFalse(result.isApproved());
        assertEquals("Cliente no autorizado", result.getMessage());
    }

    // Verifica que un cliente autorizado pueda continuar con el pago.
    @Test
    void authoridezCustomerShouldProcessPayment(){

        PaymentService paymentService = new PaymentService() {

            @Override
            public PaymentResult processPayment(String reservationId, String customerId, double amount,
                                                String paymentToken) {

                return new PaymentResult(true, "Pago procesado");
            }
        };

        CustomerVerificationService verificationService = new CustomerVerificationService();

        PaymentProxy paymentProxy = new PaymentProxy(paymentService, verificationService);

        PaymentResult result = paymentProxy.processPayment("RES-1001","CUS-1001",
                350000,"tok_approved");

        assertTrue(result.isApproved());
        assertEquals("Pago procesado", result.getMessage());
    }

}
