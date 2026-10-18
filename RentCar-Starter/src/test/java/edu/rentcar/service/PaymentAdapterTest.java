package edu.rentcar.service;

import com.rentcar.paymentclient.PaymentApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentAdapterTest {

    @Test
    void approvedPaymentShouldReturnApproved(){

        PaymentApiClient client = new PaymentApiClient("http://localhost:8080");

        PaymentAdapter paymentAdapter = new PaymentAdapter(client);

        PaymentResult result = paymentAdapter.processPayment("RES-1001", "CUS-1001",
                350000, "tok_approved");

        assertTrue(result.isApproved());
        assertEquals("Payment Approved", result.getMessage());

    }

    @Test
    void declinedPaymentShouldReturnNotApproved(){

        PaymentApiClient client = new PaymentApiClient("http://localhost:8080");

        PaymentAdapter paymentAdapter = new PaymentAdapter(client);

        PaymentResult result = paymentAdapter.processPayment("RES-1001", "CUS-1001",
                350000, "tok_declined");

        assertFalse(result.isApproved());
        assertEquals("Payment rejected", result.getMessage());
    }

    @Test
    void apiErrorShouldReturnError(){

        PaymentApiClient client = new PaymentApiClient("http://localhost:8080");

        PaymentAdapter paymentAdapter = new PaymentAdapter(client);

        PaymentResult result = paymentAdapter.processPayment("RES-1001", "CUS-1001",
                350000, "tok_api_error");

        assertFalse(result.isApproved());
        assertEquals("Error HTTP: 503", result.getMessage());
    }


}
