package edu.rentcar.service;

import edu.rentcar.model.RentalRequest;
import edu.rentcar.repository.RentalRequestRepository;

import java.util.List;

public class RentalRequestService {
    private final RentalRequestRepository rentalRequestRepository;

    public RentalRequestService(RentalRequestRepository rentalRequestRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
    }

    public List<RentalRequest> getAllRentalRequests() {
        return rentalRequestRepository.findAll();
    }

    public int getRentalRequestCount() {
        return rentalRequestRepository.count();
    }

    public RentalRequest registerRentalRequest(RentalRequest request) {
        validateRequest(request);
        return rentalRequestRepository.save(request);
    }

    private void validateRequest(RentalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Rental request cannot be null.");
        }

        requireText(request.getCustomerId(), "Customer ID");
        requireText(request.getVehicleId(), "Vehicle ID");
        requireText(request.getRentalMode(), "Rental mode");
        requireText(request.getStatus(), "Status");

        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (request.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required.");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
