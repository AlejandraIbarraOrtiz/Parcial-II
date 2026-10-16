package edu.rentcar.service;

public interface PaymentService {

    PaymentResult processPayment(String reservationId, String customerId, double amount, String paymentToken);
}
