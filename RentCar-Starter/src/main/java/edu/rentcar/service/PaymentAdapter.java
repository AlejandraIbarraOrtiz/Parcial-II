package edu.rentcar.service;

import com.rentcar.paymentclient.PaymentApiClient;
import com.rentcar.paymentclient.PaymentApiRequest;
import com.rentcar.paymentclient.PaymentApiResponse;

import java.io.IOException;

public class PaymentAdapter implements PaymentService {

    private final PaymentApiClient client;

    public PaymentAdapter(PaymentApiClient client) {

        this.client = client;
    }

    @Override
    public PaymentResult processPayment(String reservationId, String customerId, double amount,
                                        String paymentToken) {

        PaymentApiRequest request = new PaymentApiRequest(reservationId, customerId, amount,
                paymentToken);

        try {
            PaymentApiResponse response = client.pay(request);

            //Verificamos primero el código HTTP
            if (response.httpStatus() != 200){

                return new PaymentResult(false, "Error HTTP: " + response.httpStatus());
            }

            //Pago aprobado
            if (response.isApproved()) {

                return new PaymentResult(true, "Pago aprobado");
            }

            //Pago rechazado
            if (response.isDeclined()) {

                return new PaymentResult(false, "Pago rechazado");
            }

            return new PaymentResult(false, "El pago no pudo completarse");

        } catch (IOException exception) {

            return new PaymentResult(false, "Error de comunicación con la API de pagos");

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            return new PaymentResult(false, "La solicitud de pago fue interrumpida");
        }
    }
}
