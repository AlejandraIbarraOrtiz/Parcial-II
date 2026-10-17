package edu.rentcar.model;

import java.time.LocalDate;
//1. Esta clase es un builder que crea una plantilla
// para las solicitudes de alquiler


public class RentalRequest {
    private final String id;
    private final String customerId;
    private final String vehicleId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean isAditionalService;
    private final String rentalMode;
    private final String status;

    private RentalRequest(Builder builder){
        this.id = builder.id;
        this.customerId= builder.customerId;
        this.vehicleId = builder.vehicleId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.isAditionalService = builder.isAditionalService;
        this.rentalMode = builder.rentalMode;
        this.status = builder.status;

    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public boolean getAditionalService() {
        return isAditionalService;
    }

    public String getRentalMode() {
        return rentalMode;
    }

    public static class Builder{
        private String id;
        private String customerId;
        private String vehicleId;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isAditionalService;
        private String rentalMode;
        private String status;

        public Builder (
                String id,
                String customerId,
                String vehicleId,
                LocalDate startDate,
                LocalDate endDate,
                String rentalMode,
                String status
        ){
            this.id = id;
            this.customerId = customerId;
            this.vehicleId = vehicleId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.rentalMode = rentalMode;
            this.status = status;
        }
        public Builder isAitionalService(boolean isAditionalService){
            this.isAditionalService = isAditionalService;
            return this;
        }

        public RentalRequest build (){
            return new RentalRequest(this);
        }
    }

}
