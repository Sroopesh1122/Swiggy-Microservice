package com.shivu.swiggy_app.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.Deliveries;
import com.shivu.swiggy_app.exception.DeliveryException;
import com.shivu.swiggy_app.feign.DeliveryPartnerServiceClient;
import com.shivu.swiggy_app.feign.MenuServiceClient;
import com.shivu.swiggy_app.feign.OrderServiceClient;
import com.shivu.swiggy_app.feign.RestaurantServiceClient;
import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.DeliveriesRequest;
import com.shivu.swiggy_app.request.DeliveryPartner;
import com.shivu.swiggy_app.request.DeliveryStatusAndCodeVerifyRequest;
import com.shivu.swiggy_app.request.MenuItem;
import com.shivu.swiggy_app.request.Order;
import com.shivu.swiggy_app.request.OrderStatusUpdateRequest;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.User;
import com.shivu.swiggy_app.service.IDeliveriesServices;
import com.shivu.swiggy_app.service.PaginationService;
import com.shivu.swiggy_app.util.MessageReader;

import feign.FeignException;


@RestController
@RequestMapping("/api/deliveries")
public class DeliveriesController {
	
	@Autowired
	private IDeliveriesServices deliveriesServices;
	
	@Autowired
	private OrderServiceClient orderServiceClient;
	
	@Autowired
	private DeliveryPartnerServiceClient deliveryPartnerServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	@Autowired
	private RestaurantServiceClient restaurantServiceClient;
	
	@Autowired
	private MenuServiceClient menuServiceClient;
	
	@PostMapping("/create")
	public ResponseEntity<?> createDelivery(@RequestBody DeliveriesRequest request)
	{
		Deliveries deliveries = new Deliveries();
		deliveries.setAssignedAt(request.getAssignedAt());
		deliveries.setDeliver_code(request.getDeliver_code());
		deliveries.setDeliveryPartnerId(request.getDeliveryPartnerId());
		deliveries.setDeliveryStatus(request.getDeliveryStatus());
		deliveries.setOrderId(request.getOrderId());
		
		return ResponseEntity.ok(deliveriesServices.create(deliveries));
	}
	
	
	
	@GetMapping("/id/{id}")
	public ResponseEntity<?> getDeliveryById(@PathVariable Integer id) {
		
		Deliveries deliveries =deliveriesServices.getById(id);
		if(deliveries ==null)
		{
			throw new DeliveryException("Delivery Not Found");
		}
		return ResponseEntity.ok(deliveries);
		
	}

	@GetMapping("/orderId/{id}")
	public ResponseEntity<?> getDeliveryByOrderId(@PathVariable Integer id) {
		
		Deliveries deliveries =deliveriesServices.getByOrderId(id);
		if(deliveries ==null)
		{
			throw new DeliveryException("Delivery Not Found");
		}
		return ResponseEntity.ok(deliveries);
		
	}
	

	@GetMapping("/")
	public ResponseEntity<?> getDeliveries(
			@RequestParam( required = false , defaultValue = "") String status,
			@RequestParam Integer deliveryPartnerId , 
			@RequestParam(required = false ,defaultValue = "1") Integer page,
			@RequestParam(required = false , defaultValue = "10") Integer limit ){
		
		Map<String, Object> response =   new HashMap<>();
		
		page= page-1;
		
		Pageable pageable =  PageRequest.of(page, limit, Sort.by("deliveryId").descending());
		
		Page<Deliveries> deliveries = deliveriesServices.getDeliveriesByDeliveryPartnerId(deliveryPartnerId, status, pageable);
		
		System.out.println(deliveries.getContent());
		
		response.put("pagination", PaginationService.getPageData(deliveries));
		response.put("deliveries", getDeliveriesInfo(deliveries));
        
		return ResponseEntity.ok(response);
		 
	}
	
	@PutMapping("/order/verify/status")
	public ResponseEntity<?> checkForDeliveryCodeAndMarkAsDelivered(
			@RequestBody DeliveryStatusAndCodeVerifyRequest request)
	{
		
		Deliveries delivery = deliveriesServices.getById(request.getDeliveryId());
		
		if(delivery ==null)
		{
			throw new DeliveryException("Delivery Not Found");
		}
		
		if(!delivery.getDeliver_code().equals(request.getDeliveryCode()))
		{
			throw new DeliveryException("Invalid Delivery Code");
		}
		
		Order order = null;
		
		OrderStatusUpdateRequest orderStatusUpdateRequest = new OrderStatusUpdateRequest();
		
		
		try {
			order = orderServiceClient.getById(delivery.getOrderId());
		} catch (FeignException e) {
			// TODO: handle exception
			throw new DeliveryException(MessageReader.getMessage(e));
		}
		
		orderStatusUpdateRequest.setOrderId(delivery.getOrderId());
		orderStatusUpdateRequest.setStatus("delivered");
		
		
		try {
			orderServiceClient.updateOrder(orderStatusUpdateRequest);
		} catch (FeignException e) {
			// TODO: handle exception
			throw new DeliveryException(MessageReader.getMessage(e));
		}
		
		delivery.setDeliveryStatus("delivered");
		delivery.setDeliveredAt(LocalDateTime.now());
		deliveriesServices.update(delivery);
		
		Map<String, Object> response =  new HashMap<>();
		response.put("stauts", "success");
		response.put("message", "Order Delivered Successfully");
		return ResponseEntity.ok(response);
		
	}
	
    
	
	private List<Map<String, Object>> getDeliveriesInfo(Page<Deliveries> deliveriesPage)
	{
		List<Map<String , Object>> deliveryList = deliveriesPage.stream().map((delivery)->{
			Map<String,Object> deliveryMap = new HashMap<>();
			deliveryMap.put("deliveryId", delivery.getDeliveryId());
			deliveryMap.put("deliveryStatus", delivery.getDeliveryStatus());
			deliveryMap.put("assignedAt", delivery.getAssignedAt());
			deliveryMap.put("deliveryCode", delivery.getDeliver_code());
			
            //Delivery Partner info
			
			System.out.println("Delivery Details "+delivery);
			
			
			Map<String, Object> deliveryPartnerInfo =  new HashMap<>();
			DeliveryPartner deliveryPartner =  null;
			
			try {
				deliveryPartner = deliveryPartnerServiceClient.getDeliveryPartnerById(delivery.getDeliveryPartnerId());
					
			} catch (FeignException e) {
				// TODO: handle exception
				e.printStackTrace();
				throw new DeliveryException(MessageReader.getMessage(e));
			}
			
 			
			deliveryPartnerInfo.put("Name", deliveryPartner.getName());
			deliveryPartnerInfo.put("phoneNumber", deliveryPartner.getPhoneNumber());
			deliveryPartnerInfo.put("vehicleNumber", deliveryPartner.getVehicleDetails());
			
			deliveryMap.put("deliveryPartner", deliveryPartnerInfo);
			
			//Order Info
			
			Order order = null;
			
			try {
				order = orderServiceClient.getById(delivery.getOrderId());
			} catch (FeignException e) {
				// TODO: handle exception
				e.printStackTrace();
				throw new DeliveryException(MessageReader.getMessage(e));
			}
			
			Map<String, Object> orderMap = new HashMap<>();

			orderMap.put("orderId", order.getOrderId());
			orderMap.put("deliveryAddress", order.getDeliveryAddress());
			orderMap.put("totalAmount", order.getTotalAmount());
			orderMap.put("status", order.getStatus());
			orderMap.put("reviewed", order.getReviewed());
			orderMap.put("orderDate", order.getCreatedAt());
			orderMap.put("payMode", order.getPayMode());

			if (order.getUserId() != null) {
				
				User orderBy = null;
				
				try {
					orderBy = userServiceClient.getUserById(order.getUserId());
				} catch (FeignException e) {
					// TODO: handle exception
					e.printStackTrace();
					throw new DeliveryException(MessageReader.getMessage(e));
				}
				
				orderBy.setPassword(null);
				orderBy.setCreatedAt(null);
				orderBy.setEmail(null);
				orderMap.put("OrderedBy", orderBy);
			}

			if (order.getRestaurantId() != null) {
				
				Restaurant restaurant =null;
				
				try {
					restaurant = restaurantServiceClient.getRestaurantById(order.getRestaurantId());
				} catch (FeignException e) {
					// TODO: handle exception
					e.printStackTrace();
					throw new DeliveryException(MessageReader.getMessage(e));
				}
				
				restaurant.setPassword(null);
				orderMap.put("Restauarnt", restaurant);
			}
			
			System.out.println(order);

			if (order.getOrderItems() != null) {
				List<Map<String, Object>> orderItemList = order.getOrderItems().stream().map(orderItem -> {

					Map<String, Object> orderItemMap = new HashMap<>();

					orderItemMap.put("orderDetails", orderItem);
					
					MenuItem menuItem = null;
					
					System.out.println(orderItem);
					
					try {
						menuItem = menuServiceClient.getById(orderItem.getMenuItemId());
					} catch (FeignException e) {
						// TODO: handle exception
						e.printStackTrace();
						throw new DeliveryException(MessageReader.getMessage(e));
					}
					
					orderItemMap.put("MenuItem",menuItem);
					return orderItemMap;

				}).collect(Collectors.toList());

				orderMap.put("orderItem", orderItemList);

			}
			
			deliveryMap.put("order", orderMap);
			
			return deliveryMap;
		}).collect(Collectors.toList());
		
		return deliveryList;
	}
	
//	private List<Map<String, Object>> getDeliveriesInfo(Page<Deliveries> deliveriesPage) {
//	    return deliveriesPage.getContent().stream().map(delivery -> {
//	        Map<String, Object> deliveryMap = new HashMap<>();
//	        deliveryMap.put("deliveryId", delivery.getDeliveryId());
//	        deliveryMap.put("deliveryStatus", delivery.getDeliveryStatus());
//	        deliveryMap.put("assignedAt", delivery.getAssignedAt());
//	        deliveryMap.put("deliveryCode", delivery.getDeliver_code());
//
//	        // Delivery Partner info
//	        DeliveryPartner deliveryPartner = delivery.getDeliveryPartner();
//	        Map<String, Object> deliveryPartnerInfo = Optional.ofNullable(deliveryPartner)
//	            .map(dp -> {
//	                Map<String, Object> dpMap = new HashMap<>();
//	                dpMap.put("Name", dp.getName());
//	                dpMap.put("phoneNumber", dp.getPhoneNumber());
//	                dpMap.put("vehicleNumber", dp.getVehicleDetails());
//	                return dpMap;
//	            }).orElse(new HashMap<>());
//	        
//	        deliveryMap.put("deliveryPartner", deliveryPartnerInfo);
//
//	        // Order Info
//	        Order order = delivery.getOrder();
//	        Map<String, Object> orderMap = Optional.ofNullable(order)
//	            .map(o -> {
//	                Map<String, Object> oMap = new HashMap<>();
//	                oMap.put("orderId", o.getOrderId());
//	                oMap.put("deliveryAddress", o.getDeliveryAddress());
//	                oMap.put("totalAmount", o.getTotalAmount());
//	                oMap.put("status", o.getStatus());
//	                oMap.put("reviewed", o.getReviewed());
//	                oMap.put("orderDate", o.getCreatedAt());
//	                oMap.put("payMode", o.getPayMode());
//
//	                // OrderedBy (User info)
//	                Optional.ofNullable(o.getUser()).ifPresent(user -> {
//	                    Map<String, Object> userInfo = new HashMap<>();
//	                    userInfo.put("id", user.getId());
//	                    userInfo.put("name", user.getName());
//	                    oMap.put("OrderedBy", userInfo);
//	                });
//
//	                // Restaurant Info
//	                Optional.ofNullable(o.getRestaurant()).ifPresent(restaurant -> {
//	                    Map<String, Object> restaurantInfo = new HashMap<>();
//	                    restaurantInfo.put("id", restaurant.getId());
//	                    restaurantInfo.put("name", restaurant.getName());
//	                    oMap.put("Restaurant", restaurantInfo);
//	                });
//
//	                // Order Items
//	                List<Map<String, Object>> orderItemsList = Optional.ofNullable(o.getOrderItems())
//	                    .map(orderItems -> orderItems.stream().map(orderItem -> {
//	                        Map<String, Object> orderItemMap = new HashMap<>();
//	                        orderItemMap.put("orderDetails", orderItem);
//	                        orderItemMap.put("MenuItem", orderItem.getMenuItem());
//	                        return orderItemMap;
//	                    }).collect(Collectors.toList())).orElse(Collections.emptyList());
//
//	                oMap.put("orderItem", orderItemsList);
//	                return oMap;
//	            }).orElse(new HashMap<>());
//
//	        deliveryMap.put("order", orderMap);
//	        return deliveryMap;
//	    }).collect(Collectors.toList());
//	}

	
	
	
	
}

