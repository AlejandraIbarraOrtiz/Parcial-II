package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.facade.RentCarFacade;
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


import java.util.List;

public class ReservationController {
    private final ReservationService reservationService = ApplicationServices.reservationService();
    private final RentalRequestService rentalRequestService = ApplicationServices.rentalRequestService();
    private final RentCarFacade rentCarFacade = ApplicationServices.rentCarFacade();

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
    @FXML private ComboBox<String> paymentReservationCombo;
    @FXML private TextField paymentAmountField;
    @FXML private TextField paymentTokenField;

    @FXML
    public void initialize() {
        reservationIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractReservationId(cell.getValue())));
        requestIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractRentalRequestId(cell.getValue())));
        customerIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(extractCustomerId(cell.getValue())));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().description()));
        totalPriceColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(reservationService.calculateReservationTotal(cell.getValue())).asObject());

        populateRequestCombo();
        refreshReservationData();
        populatePaymentReservationCombo();
    }

    //Carga las reservas existentes en el ComboBox de pago.
    private void populatePaymentReservationCombo(){

        List<String> reservationIds = new java.util.ArrayList<>();

        for (Reservation reservation : reservationService.getAllReservations()){

            reservationIds.add(extractReservationId(reservation));
        }

        paymentReservationCombo.getItems().setAll(reservationIds);

        if (!reservationIds.isEmpty()){

            paymentReservationCombo.getSelectionModel().selectFirst();
        }

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

            Reservation createdReservation = rentCarFacade.createReservation(
                    selectedRequestId,
                    gpsCheckBox.isSelected(),
                    gpsCheckBox.isSelected() ? parsePrice(gpsPriceField.getText(), "GPS") : 0,
                    carSeatCheckBox.isSelected(),
                    carSeatCheckBox.isSelected() ? parsePrice(carSeatPriceField.getText(), "Car Seat") : 0,
                    driverCheckBox.isSelected(),
                    driverCheckBox.isSelected() ? parsePrice(driverPriceField.getText(), "Additional Driver") : 0
            );


            refreshReservationData();
            populatePaymentReservationCombo();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Reservation Created",
                    "Reservation registered successfully with ID: " + extractReservationId(createdReservation));

        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.WARNING, "Reservation Error", exception.getMessage());
        }
    }

    @FXML
    private void processPayment(){

        try {
            String reservationId = paymentReservationCombo.getValue();

            if (reservationId == null || reservationId.trim().isEmpty()){

                throw new IllegalArgumentException("Please select a reservation.");
            }

            String amountText = paymentAmountField.getText();

            if (amountText == null || amountText.trim().isEmpty()){

                throw new IllegalArgumentException("Payment amount is required.");
            }

            double amount;

            try {

                amount = Double.parseDouble(amountText.trim());
            }catch (NumberFormatException exception){

                throw new IllegalArgumentException("Payment amount must be a valid number.");
            }

            if (amount <= 0){

                throw new IllegalArgumentException("Payment amount must be greater than zero.");
            }

            String paymentToken = paymentTokenField.getText();

            if (paymentToken == null || paymentToken.trim().isEmpty()){

                throw new IllegalArgumentException("Payment token is required");
            }

            //Busca la reserva seleccionada para obtener su ID de cliente.
            Reservation selectedReservation = findReservationById(reservationId);

            if (selectedReservation == null) {

                throw new IllegalArgumentException("Reservation not found.");
            }

            String customerId = extractCustomerId(selectedReservation);

            // La Fachada coordina el proceso de pago.
            // El Proxy y el Adapter permanecen ocultos para el controlador.
            var result = rentCarFacade.processPayment(

                    reservationId,
                    customerId,
                    amount,
                    paymentToken.trim()
            );

            if (result.isApproved()) {
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Payment Approved",
                        result.getMessage()
                );
            } else {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Payment Not Approved",
                        result.getMessage()
                );
            }
        }catch (IllegalArgumentException exception){

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment Error",
                    exception.getMessage()
            );
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

    @FXML
    private void clearPaymentForm() {

        // Vuelve a seleccionar la primera reserva de la lista.
        if (!paymentReservationCombo.getItems().isEmpty()) {
            paymentReservationCombo.getSelectionModel().selectFirst();
        }

        // Limpia los datos de pago ingresados.
        paymentAmountField.clear();
        paymentTokenField.clear();
    }

    private void refreshReservationData() {
        reservationTable.getItems().setAll(reservationService.getAllReservations());
    }

    //Busca una reserva por su ID para obtener la información del cliente para el pago.
    private Reservation findReservationById(String reservationId) {

        for (Reservation reservation : reservationService.getAllReservations()) {

            if (extractReservationId(reservation).equalsIgnoreCase(reservationId)) {

                return reservation;
            }
        }

        return null;
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
