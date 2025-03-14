package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class CreateDeliveryPartnerRequest {

    private String email;

    private String name;

    private String phoneNumber;

    private String password;

    private String vehicleNumber;
}

