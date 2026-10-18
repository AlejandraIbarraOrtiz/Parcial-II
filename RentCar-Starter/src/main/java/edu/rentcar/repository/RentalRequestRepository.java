package edu.rentcar.repository;

import edu.rentcar.model.RentalRequest;

import java.util.ArrayList;
import java.util.List;

public class RentalRequestRepository {
    private final List<RentalRequest> rentalRequests = new ArrayList<>();
    private int nextId = 2001;

    public List<RentalRequest> findAll() {
        return List.copyOf(rentalRequests);
    }

    public RentalRequest save(RentalRequest request) {
        String id = "REQ-" + nextId++;

        RentalRequest requestWithId = new RentalRequest.Builder(
                id,
                request.getCustomerId(),
                request.getVehicleId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRentalMode(),
                request.getStatus()
        )
                .isAitionalService(request.getAditionalService())
                .build();

        rentalRequests.add(requestWithId);
        return requestWithId;
    }

    public int count() {
        return rentalRequests.size();
    }
}
