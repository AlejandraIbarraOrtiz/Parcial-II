package edu.rentcar.repository;

import edu.rentcar.model.RentalStore;

import java.util.ArrayList;
import java.util.List;

public class RentalModeRepository {
    private final List<RentalStore> rentalStores = new ArrayList<>();

    public List<RentalStore> findAll() {
        return List.copyOf(rentalStores);
    }

    public RentalStore saveOrUpdate(RentalStore rentalStore) {
        // Reemplaza la configuración existente para la misma modalidad o añade una nueva
        rentalStores.removeIf(store -> store.getModeName().equalsIgnoreCase(rentalStore.getModeName()));
        rentalStores.add(rentalStore);
        return rentalStore;
    }

    public int count() {
        return rentalStores.size();
    }
}