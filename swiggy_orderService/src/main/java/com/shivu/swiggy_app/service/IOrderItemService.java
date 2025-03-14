package com.shivu.swiggy_app.service;

import com.shivu.swiggy_app.entity.OrderItem;

public interface IOrderItemService
{
  public OrderItem add(OrderItem orderItem);
  public OrderItem update(OrderItem orderItem);
  public OrderItem getById(Integer id);
}
