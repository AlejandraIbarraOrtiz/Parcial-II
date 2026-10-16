package edu.rentcar.service;

import edu.rentcar.model.RentalMode;
import edu.rentcar.model.RentalRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalRequestService {

    private List<RentalRequest> requests;

    public RentalRequestService(){
        this.requests = new ArrayList<>();
    }

    public void createRequest(
            String id,
            String costumerId,
            String vehicleId,
            LocalDate startDate,
            LocalDate endDate,
            RentalMode rentalMode,
            String status,
            boolean isAditionalServices){
        if (isAditionalServices){
            RentalRequest request = new RentalRequest.Builder(
                    id,
                    costumerId,
                    vehicleId,
                    startDate,
                    endDate,rentalMode,
                    status
            )
                    .isAitionalService(true)
                    .build();

            requests.add(request);
        }else{
            RentalRequest request = new RentalRequest.Builder(
                    id,
                    costumerId,
                    vehicleId,
                    startDate,
                    endDate,rentalMode,
                    status
            )
                    .isAitionalService(false)
                    .build();

            requests.add(request);
        }

    }
}
