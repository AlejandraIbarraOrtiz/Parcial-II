package edu.rentcar.service;

// Proxy: verifica al cliente antes depermitir el procesamiento de pago
public class PaymentProxy implements  PaymentService{

    private PaymentService paymentService;
    private CustomerVerificationService verificationService;

    public PaymentProxy(PaymentService paymentService, CustomerVerificationService verificationService){

        this.paymentService = paymentService;
        this.verificationService = verificationService;
    }

    // Verifica al cliente y solo permite procesar el pago si está autorizado.
    @Override
    public PaymentResult processPayment(String reservationId, String customerId, double amount,
                                        String paymentToken){

        //  Si el cliente no está autorizado, el pago no continúa.
        if (!verificationService.verify(customerId)){

            return new PaymentResult(false, "Cliente no autorizado");
        }

        // Si está autorizado, el Proxy delega el pago al servicio real.
        return paymentService.processPayment(reservationId, customerId, amount, paymentToken);
    }
}
