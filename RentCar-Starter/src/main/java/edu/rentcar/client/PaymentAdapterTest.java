package edu.rentcar.client;

import com.rentcar.paymentclient.PaymentApiClient;
import edu.rentcar.service.PaymentAdapter;
import edu.rentcar.service.PaymentResult;

public class PaymentAdapterTest {
    public static void main(String[] args) {

        PaymentApiClient client =
                new PaymentApiClient("http://localhost:8080");

        PaymentAdapter paymentAdapter =
                new PaymentAdapter(client);

        PaymentResult result = paymentAdapter.processPayment(
                "RES-1001",
                "CUS-1001",
                350000,
                "tok_api_error"
        );

        System.out.println("Resultado: " + result.getMessage());
        System.out.println("¿Aprobado?: " + result.isApproved());
    }
}
