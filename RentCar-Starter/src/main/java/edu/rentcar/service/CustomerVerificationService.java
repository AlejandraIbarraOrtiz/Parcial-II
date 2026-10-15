package edu.rentcar.service;

import edu.rentcar.data.SampleData;
import edu.rentcar.model.Customer;

// Servicio engargado de verificar si un cliente está autorizado para realizar un pago.
public class CustomerVerificationService {

    public boolean verify(String customerId){

        for (Customer customer : SampleData.customers()){

            if (customer.getId().equals(customerId)){

                return customer.isAuthorized();
            }
        }
        return false;
    }
}
