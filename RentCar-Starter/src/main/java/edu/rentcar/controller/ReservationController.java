package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.model.NormalReservation;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationDecorator;
import edu.rentcar.service.RentalRequestService;
import edu.rentcar.service.ReservationService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ReservationController {
    private final ReservationService reservationService = ApplicationServices.reservationService();
    private final RentalRequestService rentalRequestService = ApplicationServices.rentalRequestService();

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> requestIdColumn;
    @FXML private TableColumn<Reservation, String> customerIdColumn;
    @FXML private TableColumn<Reservation, String> descriptionColumn;
    @FXML private TableColumn<Reservation, Double> totalPriceColumn;

    @FXML private ComboBox<String> requestIdCombo;
    @FXML private CheckBox gpsCheckBox;
    @FXML private TextField gpsPriceField;
    @FXML private CheckBox carSeatCheckBox;
    @FXML private TextField carSeatPriceField;
    @FXML private CheckBox driverCheckBox;
    @FXML private TextField driverPriceField;

    @FXML
    public void initialize() {
        reservationIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractReservationId(cell.getValue())));
        requestIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractRentalRequestId(cell.getValue())));
        customerIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractCustomerId(cell.getValue())));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().description()));
        totalPriceColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(reservationService.calculateReservationTotal(cell.getValue())).asObject());

        populateRequestCombo();
        refreshReservationData();
    }

    private void populateRequestCombo() {
        List<String> requestIds = rentalRequestService.getAllRentalRequests().stream()
                .map(RentalRequest::getId)
                .toList();

        requestIdCombo.getItems().setAll(requestIds);
        if (!requestIds.isEmpty()) {
            requestIdCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void createReservation() {
        try {
            String selectedRequestId = requestIdCombo.getValue();
            if (selectedRequestId == null || selectedRequestId.trim().isEmpty()) {
                throw new IllegalArgumentException("Please select a valid Rental Request.");
            }

            List<Function<Reservation, Reservation>> selectedDecorators = new ArrayList<>();

            // Si se marca la casilla GPS, validamos e incluimos su precio dinámico
            if (gpsCheckBox.isSelected()) {
                double gpsPrice = parsePrice(gpsPriceField.getText(), "GPS");
                selectedDecorators.add(reservationService.withGPS(gpsPrice));
            }

            // Si se marca Car Seat, validamos e incluimos su precio dinámico
            if (carSeatCheckBox.isSelected()) {
                double carSeatPrice = parsePrice(carSeatPriceField.getText(), "Car Seat");
                selectedDecorators.add(reservationService.withCarSeat(carSeatPrice));
            }

            // Si se marca Additional Driver, validamos e incluimos su precio dinámico
            if (driverCheckBox.isSelected()) {
                double driverPrice = parsePrice(driverPriceField.getText(), "Additional Driver");
                selectedDecorators.add(reservationService.withAdditionalDriver(driverPrice));
            }

            Reservation createdReservation = reservationService.createReservation(selectedRequestId, selectedDecorators);

            refreshReservationData();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Reservation Created",
                    "Reservation registered successfully with ID: " + extractReservationId(createdReservation));

        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.WARNING, "Reservation Error", exception.getMessage());
        }
    }

    private double parsePrice(String textValue, String serviceName) {
        if (textValue == null || textValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Price for " + serviceName + " is required when selected.");
        }
        try {
            double price = Double.parseDouble(textValue.trim());
            if (price < 0) {
                throw new IllegalArgumentException("Price for " + serviceName + " cannot be negative.");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price for " + serviceName + " must be a valid number.");
        }
    }

    @FXML
    private void clearForm() {
        if (!requestIdCombo.getItems().isEmpty()) {
            requestIdCombo.getSelectionModel().selectFirst();
        }
        gpsCheckBox.setSelected(false);
        gpsPriceField.clear();
        carSeatCheckBox.setSelected(false);
        carSeatPriceField.clear();
        driverCheckBox.setSelected(false);
        driverPriceField.clear();
    }

    private void refreshReservationData() {
        reservationTable.getItems().setAll(reservationService.getAllReservations());
    }

    private String extractReservationId(Reservation reservation) {
        NormalReservation base = unwrapBaseReservation(reservation);
        return base != null ? base.getId() : "N/A";
    }

    private String extractRentalRequestId(Reservation reservation) {
        NormalReservation base = unwrapBaseReservation(reservation);
        return (base != null && base.getRentalRequest() != null) ? base.getRentalRequest().getId() : "N/A";
    }

    private String extractCustomerId(Reservation reservation) {
        NormalReservation base = unwrapBaseReservation(reservation);
        return (base != null && base.getRentalRequest() != null) ? base.getRentalRequest().getCustomerId() : "N/A";
    }

    private NormalReservation unwrapBaseReservation(Reservation res) {
        Reservation current = res;
        while (current instanceof ReservationDecorator decorator) {
            try {
                var field = ReservationDecorator.class.getDeclaredField("reservation");
                field.setAccessible(true);
                current = (Reservation) field.get(decorator);
            } catch (Exception e) {
                break;
            }
        }
        if (current instanceof NormalReservation normal) {
            return normal;
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
