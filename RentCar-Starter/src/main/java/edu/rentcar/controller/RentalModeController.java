package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.model.EconomyMode;
import edu.rentcar.model.ExecutiveMode;
import edu.rentcar.model.PremiumMode;
import edu.rentcar.model.RentalMode;
import edu.rentcar.model.RentalStore;
import edu.rentcar.service.RentalModeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.function.Function;

public class RentalModeController {
    private final RentalModeService rentalModeService = ApplicationServices.rentalModeService();

    @FXML private TableView<RentalStore> rentalModeTable;
    @FXML private TableColumn<RentalStore, String> modeNameColumn;
    @FXML private TableColumn<RentalStore, Double> rateColumn;
    @FXML private TableColumn<RentalStore, Double> mileageColumn;
    @FXML private TableColumn<RentalStore, String> medicalPolicyColumn;
    @FXML private TableColumn<RentalStore, String> roadsideAssistanceColumn;

    @FXML private ComboBox<String> modeSelectCombo;
    @FXML private TextField rateField;
    @FXML private TextField mileageField;
    @FXML private TextField medicalPolicyField;
    @FXML private TextField roadsideAssistanceField;

    private List<Class<? extends RentalMode>> modeClasses;

    @FXML
    public void initialize() {
        modeNameColumn.setCellValueFactory(new PropertyValueFactory<>("modeName"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("configuredRate"));
        mileageColumn.setCellValueFactory(new PropertyValueFactory<>("configuredMileage"));
        medicalPolicyColumn.setCellValueFactory(new PropertyValueFactory<>("configuredPolicy"));
        roadsideAssistanceColumn.setCellValueFactory(new PropertyValueFactory<>("configuredAssistance"));

        // Mapeo dinámico de opciones usando Function
        modeClasses = List.of(
                EconomyMode.class,
                ExecutiveMode.class,
                PremiumMode.class
        );

        Function<Class<? extends RentalMode>, String> getClassName = Class::getSimpleName;
        List<String> modeNames = modeClasses.stream()
                .map(getClassName)
                .toList();

        modeSelectCombo.getItems().setAll(modeNames);
        modeSelectCombo.getSelectionModel().selectFirst();

        refreshRentalModeData();
    }

    @FXML
    private void saveModeConfiguration() {
        try {
            String selectedModeName = modeSelectCombo.getValue();

            // Instanciación dinámica según la selección del usuario
            RentalMode selectedMode = modeClasses.stream()
                    .filter(clazz -> clazz.getSimpleName().equals(selectedModeName))
                    .findFirst()
                    .map(clazz -> {
                        try {
                            return clazz.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Error instantiating mode: " + selectedModeName);
                        }
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Invalid mode selected."));

            // Envío de los datos dinámicos capturados en los TextFields
            rentalModeService.configureRentalMode(
                    selectedMode,
                    rateField.getText(),
                    mileageField.getText(),
                    medicalPolicyField.getText(),
                    roadsideAssistanceField.getText()
            );

            refreshRentalModeData();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Configuration Saved",
                    "Custom settings for " + selectedModeName + " were registered successfully.");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.WARNING, "Check input values", exception.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        rateField.clear();
        mileageField.clear();
        medicalPolicyField.clear();
        roadsideAssistanceField.clear();
        modeSelectCombo.getSelectionModel().selectFirst();
    }

    private void refreshRentalModeData() {
        rentalModeTable.getItems().setAll(rentalModeService.getAllRentalStores());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}