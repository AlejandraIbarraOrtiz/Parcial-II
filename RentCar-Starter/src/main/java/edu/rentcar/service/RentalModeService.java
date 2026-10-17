package edu.rentcar.service;

import edu.rentcar.model.RentalMode;
import edu.rentcar.model.RentalStore;
import edu.rentcar.repository.RentalModeRepository;

import java.util.List;

public class RentalModeService {
    private final RentalModeRepository rentalModeRepository;

    public RentalModeService(RentalModeRepository rentalModeRepository) {
        this.rentalModeRepository = rentalModeRepository;
    }

    public List<RentalStore> getAllRentalStores() {
        return rentalModeRepository.findAll();
    }

    public RentalStore configureRentalMode(RentalMode mode, String rateText, String mileageText,
                                           String policyText, String assistanceText) {
        if (mode == null) {
            throw new IllegalArgumentException("Rental mode is required.");
        }

        double rate;
        try {
            rate = Double.parseDouble(requireText(rateText, "Daily rate"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Daily rate must be a valid number.");
        }
        if (rate <= 0) {
            throw new IllegalArgumentException("Daily rate must be greater than zero.");
        }

        double mileage;
        try {
            mileage = Double.parseDouble(requireText(mileageText, "Mileage limit"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mileage limit must be a valid number.");
        }
        if (mileage < 0) {
            throw new IllegalArgumentException("Mileage limit cannot be negative.");
        }

        String cleanPolicy = requireText(policyText, "Medical policy");
        String cleanAssistance = requireText(assistanceText, "Roadside assistance");

        // Instancia RentalStore y aplica los valores definidos dinámicamente por el usuario
        RentalStore store = new RentalStore(mode);
        store.setModes(rate, mileage, cleanPolicy, cleanAssistance);

        return rentalModeRepository.saveOrUpdate(store);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}