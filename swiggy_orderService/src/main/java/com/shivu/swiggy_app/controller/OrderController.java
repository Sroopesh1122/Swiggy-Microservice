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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.swiggy_app.entity.Order;
import com.shivu.swiggy_app.entity.OrderItem;
import com.shivu.swiggy_app.exception.OrderException;
import com.shivu.swiggy_app.feign.CartServiceCient;
import com.shivu.swiggy_app.feign.DeliveriesServiceClient;
import com.shivu.swiggy_app.feign.DeliveryPartnerServiceClient;
import com.shivu.swiggy_app.feign.EmailServiceClient;
import com.shivu.swiggy_app.feign.MenuServiceClient;
import com.shivu.swiggy_app.feign.RestaurantServiceClient;
import com.shivu.swiggy_app.feign.UserServiceClient;
import com.shivu.swiggy_app.request.Deliveries;
import com.shivu.swiggy_app.request.DeliveryPartner;
import com.shivu.swiggy_app.request.MenuItem;
import com.shivu.swiggy_app.request.OrderStatusUpdateRequest;
import com.shivu.swiggy_app.request.PickOrderRequest;
import com.shivu.swiggy_app.request.PlaceOrderRequest;
import com.shivu.swiggy_app.request.Restaurant;
import com.shivu.swiggy_app.request.SendEmailRequest;
import com.shivu.swiggy_app.request.User;
import com.shivu.swiggy_app.service.DeliveryDetails;
import com.shivu.swiggy_app.service.IOrderItemService;
import com.shivu.swiggy_app.service.IOrderService;
import com.shivu.swiggy_app.service.PaginationService;
import com.shivu.swiggy_app.util.MessageReader;
import com.shivu.swiggy_app.util.RandomGenerator;

import feign.FeignException;


@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@Autowired
	private MenuServiceClient menuServiceClient;

	@Autowired
	private RestaurantServiceClient restaurantServiceClient;

	@Autowired
	private IOrderItemService orderItemService;

	@Autowired
	private CartServiceCient cartServiceCient;

	@Autowired
	private DeliveryPartnerServiceClient deliveryServiceClient;

	@Autowired
	private DeliveriesServiceClient deliveriesServiceClient;

	@Autowired
	private EmailServiceClient emailServiceClient;

	@Autowired
	private UserServiceClient userServiceClient;

	@PostMapping("/")
	@Transactional
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> createOrder(
			@RequestBody PlaceOrderRequest request) {

		User user = null;
		
		try {
			user = userServiceClient.getUserById(request.getUserId());
		} catch (FeignException e) {
			throw new OrderException(MessageReader.getMessage(e));
		}
		

		request.getItems().stream().forEach(o -> {

			MenuItem menuItem = null;
			
			try {
				menuItem  = menuServiceClient.getById(o.getItemId());
			} catch (FeignException e) {
				// TODO: handle exception
				throw new OrderException(MessageReader.getMessage(e));
			}
	
			Restaurant restaurant = null;
			
			try {
				restaurant = restaurantServiceClient.getRestaurantById(menuItem.getRestaurantId());
			} catch (FeignException e) {
				// TODO: handle exception
				throw new OrderException(MessageReader.getMessage(e));
			}
			
			
			Order order = new Order();
			order.setCreatedAt(LocalDateTime.now());
			order.setDeliveryAddress(request.getDeliveryAddress());
			order.setPayMode(request.getMode());
			order.setRazorpayId(request.getRazorPayId());
			order.setRestaurantId(menuItem.getRestaurantId());
			order.setReviewed(0);
			order.setStatus("Pending");
			order.setTotalAmount(menuItem.getDiscount() > 0
					? (menuItem.getPrice() - (menuItem.getPrice() * (menuItem.getDiscount() / 100)))
					: menuItem.getPrice());
			order.setUserId(request.getUserId());
			order = orderService.createOrder(order);

			OrderItem orderItem = new OrderItem();
			orderItem.setMenuItemId(menuItem.getItemId());
			orderItem.setOrder(order);
			orderItem.setPrice(menuItem.getDiscount() > 0
					? (menuItem.getPrice() - (menuItem.getPrice() * (menuItem.getDiscount() / 100)))
					: menuItem.getPrice());
			orderItem.setQuantity(o.getQuantity());
			orderItem = orderItemService.add(orderItem);

		});

		if (request.getSource().equals("cart")) {
			System.out.println("Cart here");
			cartServiceCient.deleteCartItemsByUserId(request.getUserId());
		}

		Map<String, String> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "Order Placed success");
		return ResponseEntity.ok(response);

	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> getAllOrdersByUser(
			@RequestParam(defaultValue = "1", required = false) Integer page,
			@RequestParam(defaultValue = "10", required = false) Integer limit,
			@RequestParam Integer userId) {
		

		User user = null;
		
		
		try {
			user = userServiceClient.getUserById(userId);
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new OrderException(MessageReader.getMessage(e));
		}

		System.out.println(user);

		page = page - 1;
		Pageable pageable = PageRequest.of(page, limit, Sort.by("orderId").descending());

		Page<Order> orders = orderService.getAllOrdersByUserId(user.getUserId(), pageable);
       
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");

		List<Map<String, Object>> orderList = getOrderAllInfo(orders);

		
		response.put("orders", orderList);

		Map<String, Object> paginationMap = PaginationService.getPageData(orders);

		response.put("pagination", paginationMap);
		
		return ResponseEntity.ok(response);

	}

	@GetMapping("/not-delivered")
	public ResponseEntity<?> getAllUndeliveredOrders(
			@RequestParam(defaultValue = "1", required = false) Integer page,
			@RequestParam(defaultValue = "10", required = false) Integer limit,
			@RequestParam(required = false) String q) {

		Map<String, Object> response = new HashMap<>();

		page = page - 1;

		Pageable pageable = PageRequest.of(page, limit, Sort.by("orderId").descending());

		Page<Order> orders = orderService.getAllUndevlieredOrders(q, pageable);

		List<Map<String, Object>> orderList = getOrderAllInfo(orders);

		response.put("orders", orderList);
		response.put("pagination", PaginationService.getPageData(orders));

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/orderId")
	public ResponseEntity<?> getOrderItemByOrderId(@RequestParam Integer orderId) {
		Order order = orderService.getOrderById(orderId);

		if(order ==null)
		{
			throw new OrderException("Order not found");
		}
		
		order.setOrderItems(order.getOrderItems());
	

		return ResponseEntity.ok(order);
	}
	
	

	@GetMapping("/")
	public ResponseEntity<?> getOrderByOrderId(@RequestParam Integer orderId) {
		Map<String, Object> response = new HashMap<>();
		Order order = orderService.getOrderById(orderId);

		if(order ==null)
		{
			throw new OrderException("Order not found");
		}
		
		response.put("orderDetails", order);
		if (order.getUserId() != null) {
			
			User orderBy = null;
			
			try {
				orderBy = userServiceClient.getUserById(order.getUserId());
			} catch (FeignException e) {
				// TODO: handle exception
				throw new OrderException(MessageReader.getMessage(e));
			}
			
			orderBy.setPassword(null);
			response.put("OrderBy", orderBy);
		}

		if (order.getRestaurantId() != null) {
			
			Restaurant restaurant = null;
			
			try {
				restaurant = restaurantServiceClient.getRestaurantById(order.getRestaurantId());
			} catch (FeignException e) {
				// TODO: handle exception
				throw new OrderException(MessageReader.getMessage(e));
			}
			
			restaurant.setPassword(null);
			response.put("restaurant", restaurant);
		}
		if (order.getOrderItems().size() > 0) {
			List<Map<String, Object>> orderItemMap = order.getOrderItems().stream().map(orderItem -> {
				Map<String, Object> orderItemAndMenuItemMap = new HashMap<>();
				orderItemAndMenuItemMap.put("orderItemDetails", orderItem);
				
				MenuItem menuItem = null;
				
				try {
					menuItem = menuServiceClient.getById(orderItem.getMenuItemId());
				} catch (FeignException e) {
					// TODO: handle exception
					throw new OrderException(MessageReader.getMessage(e));
				}
				
				orderItemAndMenuItemMap.put("menuItem",menuItem);
				return orderItemAndMenuItemMap;
			}).collect(Collectors.toList());

			response.put("orderItems", orderItemMap);

		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/restaurant")
	@PreAuthorize("hasRole('RESTAURANT')")
	public ResponseEntity<?> getOrdersByRestaurant(
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer limit,
			@RequestParam(required = false, defaultValue = "pending") String statusFilter,
			@RequestParam Integer restaurantId) {


		page = page - 1;
		Restaurant restaurant = null;
		
		try {
			restaurant = restaurantServiceClient.getRestaurantById(restaurantId);
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new OrderException(MessageReader.getMessage(e));
		}

		Pageable pageable = PageRequest.of(page, limit, Sort.by("orderId").descending());
		Page<Order> orderPage = orderService.getAllOrdersByRestaurant(restaurantId, statusFilter, pageable);
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("totalPages", orderPage.getTotalPages());
		response.put("totalElements", orderPage.getTotalElements());
		response.put("currentPage", orderPage.getNumber());
		response.put("pageSize", orderPage.getSize());
		response.put("hasNext", orderPage.hasNext());
		response.put("hasPrevious", orderPage.hasPrevious());

		List<Map<String, Object>> ordersList = getOrderAllInfo(orderPage);

		response.put("orders", ordersList);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/status")
	@PreAuthorize("hasAnyRole('RESTAURANT','DELIVERY')")
	public ResponseEntity<?> updateOrderStatus(
			@RequestBody OrderStatusUpdateRequest request) {
		

		Order findOrder = orderService.getOrderById(request.getOrderId());
		if (findOrder == null) {
			throw new OrderException("Order not found");
		}
		findOrder.setStatus(request.getStatus());
		findOrder = orderService.updateOrder(findOrder);
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "Order Stauts updated");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/pick")
	@PreAuthorize("hasRole('DELIVERY')")
	public ResponseEntity<?> pickOrder(@AuthenticationPrincipal DeliveryDetails deliveryDetails ,@RequestBody PickOrderRequest request) {

		Order findOrder = orderService.getOrderById(request.getOrderId());
		if (findOrder == null) {
			throw new OrderException("Order Not Found");
		}

		Map<String, Object> response = new HashMap<>();

		DeliveryPartner deliveryPartner = null;
		
		try {
			deliveryPartner = deliveryServiceClient.getById(request.getDeliveryPartnerId());
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new OrderException(MessageReader.getMessage(e));
		}


		findOrder.setStatus("out of delivery");
		findOrder.setPickedBy(deliveryPartner.getPartnerId());
		orderService.updateOrder(findOrder);

		Deliveries deliveries = new Deliveries();
		deliveries.setDeliveryStatus("out of delivery");
		deliveries.setAssignedAt(LocalDateTime.now());
		deliveries.setDeliver_code(RandomGenerator.generateCharacterString(6));

		User user = null;
		
		try {
			user = userServiceClient.getUserById(findOrder.getUserId());
		} catch (FeignException e) {
			// TODO: handle exception
			throw new OrderException(MessageReader.getMessage(e));
		}

		
		MenuItem deliveryItem= null;
		Restaurant orderedRestaurant =  null;
		
		
		try {
			deliveryItem = menuServiceClient.getById(findOrder.getOrderItems().get(0).getMenuItemId());
			orderedRestaurant = restaurantServiceClient.getRestaurantById(findOrder.getRestaurantId());
		} catch (FeignException e) {
			// TODO: handle exception
			throw  new OrderException(MessageReader.getMessage(e));
		}
		
		String emailSubject = "Your Order is on the Way! 🚀 | Delivery Code Inside";

		String emailBody = "Dear " + user.getName() + ",\r\n" + "\r\n"
				+ "Thank you for ordering with us! Your delicious food is being prepared and will be delivered shortly. Below are the details of your order:\r\n"
				+ "\r\n" + "🍽 Order Details:\r\n" + "Food Item: "
				+ deliveryItem.getName() + "\r\n" + "Restaurant: "
				+ orderedRestaurant.getName()+ "\r\n" + "Total Amount: "
				+ findOrder.getTotalAmount() + "\r\n" + "🚴‍♂️ Delivery Details:\r\n" + "Delivery Partner: "
				+ deliveryPartner.getName() + "\r\n" + "Contact: " + deliveryPartner.getPhoneNumber() + "\r\n"
				+ "Estimated Arrival: Soon\r\n" + "🔑 Your Delivery Code: " + deliveries.getDeliver_code() + "\r\n"
				+ "Please share this code with the delivery partner upon receiving your order to confirm the delivery.\r\n"
				+ "\r\n" + "If you have any questions, feel free to contact our support team.\r\n" + "\r\n"
				+ "Enjoy your meal! 🍕🍔🍜\r\n" + "\r\n" + "Best Regards,\r\n" + "SWIGGY\r\n" + "6362379895";
		
		String htmlBody = "<html>"
		        + "<body style='font-family: Arial, sans-serif; color: #fff; line-height: 1.6;'>"
		        + "<p>Dear <strong>" + user.getName() + "</strong>,</p>"

		        + "<p>Thank you for ordering with us! Your delicious food is being prepared and will be delivered shortly. Below are the details of your order:</p>"

		        + "<h3>&#127869; Order Details:</h3>"  // 🍽 (Food Emoji)
		        + "<p><strong>Food Item:</strong> " + deliveryItem.getName() + "<br>"
		        + "<strong>Restaurant:</strong> " + orderedRestaurant.getName() + "<br>"
		        + "<strong>Total Amount:</strong> ₹" + findOrder.getTotalAmount() + "</p>"

		        + "<h3>&#128692;&#8205;&#9794;&#65039; Delivery Details:</h3>"  // 🚴‍♂️ (Delivery Emoji)
		        + "<p><strong>Delivery Partner:</strong> " + deliveryPartner.getName() + "<br>"
		        + "<strong>Contact:</strong> " + deliveryPartner.getPhoneNumber() + "<br>"
		        + "<strong>Estimated Arrival:</strong> Soon</p>"

		        + "<h3>&#128273; Your Delivery Code:</h3>"  // 🔑 (Key Emoji)
		        + "<p style='font-size: 18px; font-weight: bold; color: #d9534f;'>" + deliveries.getDeliver_code() + "</p>"
		        + "<p>Please share this code with the delivery partner upon receiving your order to confirm the delivery.</p>"

		        + "<p>If you have any questions, feel free to contact our support team.</p>"

		        + "<p>Enjoy your meal! &#127829;&#127828;&#127836;</p>"  // 🍕🍔🍜 (Food Emojis)

		        + "<p><strong>Best Regards,</strong><br>"
		        + "SWIGGY<br>"
		        + "<strong>Contact:</strong> <a href='tel:6362379895' style='color: #007BFF; text-decoration: none;'>6362379895</a></p>"

		        + "</body></html>";



		deliveries.setDeliveryPartnerId(deliveryPartner.getPartnerId());
		deliveries.setOrderId(findOrder.getOrderId());

		try {
			deliveriesServiceClient.createDelivery(deliveries);
			
		} catch (FeignException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new OrderException(MessageReader.getMessage(e));
		}

		SendEmailRequest sendEmailRequest =new SendEmailRequest();
		sendEmailRequest.setBody(htmlBody);
		sendEmailRequest.setEmail(user.getEmail());
		sendEmailRequest.setSubject(emailSubject);
		
		try {
			emailServiceClient.sendEmail(sendEmailRequest);
		} catch (FeignException e) {
			e.printStackTrace();
			throw new OrderException(MessageReader.getMessage(e));
		}

		response.put("status", "success");
		response.put("message", "Order Picked Successfully");

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateOrder(@RequestBody Order request)
	{
		Order order = orderService.getOrderById(request.getOrderId());
		
		if(order ==null)
		{
			throw new OrderException("Order not found");
		}
		
		order.setReviewed(request.getReviewed());
		order.setStatus(request.getStatus());
	    
		order = orderService.updateOrder(order);
		
		return ResponseEntity.ok(order);
		
	}
	
	
	private List<Map<String, Object>> getOrderAllInfo(Page<Order> orders) {
		List<Map<String, Object>> orderList = orders.stream().map(order -> {
			Map<String, Object> orderMap = new HashMap<>();

			orderMap.put("orderId", order.getOrderId());
			orderMap.put("deliveryAddress", order.getDeliveryAddress());
			orderMap.put("totalAmount", order.getTotalAmount());
			orderMap.put("status", order.getStatus());
			orderMap.put("reviewed", order.getReviewed());
			orderMap.put("orderDate", order.getCreatedAt());
			orderMap.put("payMode", order.getPayMode());

			if (order.getUserId() != null) {
				
				User orderBy =null;
				
				try {
					orderBy = userServiceClient.getUserById(order.getUserId());
				} catch (FeignException e) {
					// TODO: handle exception
					e.printStackTrace();
					throw new OrderException(MessageReader.getMessage(e));
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
					throw  new OrderException(MessageReader.getMessage(e));
				}
				
				restaurant.setPassword(null);
				orderMap.put("Restauarnt", restaurant);
			}

			if (order.getOrderItems() != null) {
				List<Map<String, Object>> orderItemList = order.getOrderItems().stream().map(orderItem -> {

					Map<String, Object> orderItemMap = new HashMap<>();

					orderItemMap.put("orderDetails", orderItem);
					
					MenuItem menuItem = null;
					
					try {
						menuItem = menuServiceClient.getById(orderItem.getMenuItemId());
					} catch (FeignException e) {
						// TODO: handle exception
						e.printStackTrace();
						throw new OrderException(MessageReader.getMessage(e));
					}
					
					orderItemMap.put("MenuItem", menuItem);
					
					if(order.getPickedBy()!=null)
					{
						Deliveries deliveries = null;
						
						try {
							deliveries =deliveriesServiceClient.getByOrderId(orderItem.getOrder().getOrderId());
						} catch (FeignException e) {
							// TODO: handle exception
							e.printStackTrace();
							throw new OrderException(MessageReader.getMessage(e));
						}
						
						orderItemMap.put("deliveryDetails",deliveries);
						
						orderItemMap.put("pickedBy",deliveryServiceClient.getById(order.getPickedBy()));
					}
					
					return orderItemMap;

				}).collect(Collectors.toList());

				orderMap.put("orderItem", orderItemList);

			}

			return orderMap;

		}).collect(Collectors.toList());

		return orderList;
	}

}
