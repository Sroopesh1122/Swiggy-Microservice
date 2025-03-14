package com.shivu.swiggy_app.request;

import lombok.Data;

@Data
public class PickOrderRequest
{
   private Integer orderId;
   private Integer deliveryPartnerId;
}
