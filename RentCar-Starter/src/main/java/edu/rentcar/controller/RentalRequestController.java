package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.model.EconomyMode;
import edu.rentcar.model.ExecutiveMode;
import edu.rentcar.model.PremiumMode;
import edu.rentcar.model.RentalMode;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.service.RentalRequestService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class RentalRequestController {
    private final RentalRequestService rentalRequestService = ApplicationServices.rentalRequestService();

    @FXML private TableView<RentalRequest> rentalRequestTable;
    @FXML private TableColumn<RentalRequest, String> requestIdColumn;
    @FXML private TableColumn<RentalRequest, String> customerIdColumn;
    @FXML private TableColumn<RentalRequest, String> vehicleIdColumn;
    @FXML private TableColumn<RentalRequest, LocalDate> startDateColumn;
    @FXML private TableColumn<RentalRequest, LocalDate> endDateColumn;
    @FXML private TableColumn<RentalRequest, String> rentalModeColumn;
    @FXML private TableColumn<RentalRequest, String> statusColumn;
    @FXML private TableColumn<RentalRequest, Boolean> aditionalServiceColumn;

    @FXML private TextField customerIdField;
    @FXML private TextField vehicleIdField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> rentalModeCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private CheckBox aditionalServiceCheckBox;

    @FXML
    public void initialize() {
        // 1. Vincular columnas de la tabla con los getters de RentalRequest
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        rentalModeColumn.setCellValueFactory(new PropertyValueFactory<>("rentalMode"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        aditionalServiceColumn.setCellValueFactory(new PropertyValueFactory<>("aditionalService"));

        // 2. Usamos Function para extraer dinámicamente los nombres de las clases RentalMode
        List<Class<? extends RentalMode>> modeClasses = List.of(
                EconomyMode.class,
                ExecutiveMode.class,
                PremiumMode.class
        );

        // Definimos la función que transforma la referencia de clase en su nombre simple
        Function<Class<? extends RentalMode>, String> getClassName = Class::getSimpleName;

        // Mapeamos la lista de clases a una lista de String ("EconomyMode", "ExecutiveMode", etc.)
        List<String> modeNames = modeClasses.stream()
                .map(getClassName)
                .toList();

        // Cargamos el ComboBox con los nombres obtenidos
        rentalModeCombo.getItems().setAll(modeNames);
        rentalModeCombo.getSelectionModel().selectFirst();

        // 3. Cargamos los estados posibles de la solicitud
        statusCombo.getItems().setAll("Pending", "Approved", "Rejected", "Completed");
        statusCombo.getSelectionModel().selectFirst();

        refreshRentalRequestData();
    }

    @FXML
    private void registerRentalRequest() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            // Construimos el objeto RentalRequest utilizando el Builder
            RentalRequest request = new RentalRequest.Builder(
                    null,
                    customerIdField.getText(),
                    vehicleIdField.getText(),
                    startDate,
                    endDate,
                    rentalModeCombo.getValue(),
                    statusCombo.getValue()
            )
                    .isAitionalService(aditionalServiceCheckBox.isSelected())
                    .build();

            RentalRequest savedRequest = rentalRequestService.registerRentalRequest(request);

            refreshRentalRequestData();
            clearRentalRequestForm();
            showAlert(Alert.AlertType.INFORMATION, "Rental Request Registered",
                    "Request " + savedRequest.getId() + " was registered successfully.");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.WARNING, "Check request information", exception.getMessage());
        }
    }

    @FXML
    private void clearRentalRequestForm() {
        customerIdField.clear();
        vehicleIdField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        rentalModeCombo.getSelectionModel().selectFirst();
        statusCombo.getSelectionModel().selectFirst();
        aditionalServiceCheckBox.setSelected(false);
    }

    private void refreshRentalRequestData() {
        rentalRequestTable.getItems().setAll(rentalRequestService.getAllRentalRequests());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}