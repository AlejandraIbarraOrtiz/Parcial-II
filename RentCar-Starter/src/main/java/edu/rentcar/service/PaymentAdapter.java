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

                return new PaymentResult(true, "Payment Approved");
            }

            //Pago rechazado
            if (response.isDeclined()) {

                return new PaymentResult(false, "Payment rejected");
            }

            return new PaymentResult(false, "Payment could not be completed");

        } catch (IOException exception) {

            return new PaymentResult(false, "Error communicating with the payment API");

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            return new PaymentResult(false, "Payment request was interrumped");
        }
    }
}
