<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.tap.model.Order"%>
<%@ page import="com.tap.model.OrderItem"%>
<%@ page import="com.tap.model.Menu"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.DaoImp.OrderItemDaoImp"%>
<%@ page import="com.tap.DaoImp.MenuDaoImp"%>
<%@ page import="com.tap.DaoImp.RestaurantDaoImp"%>
<%@ page import="com.tap.DaoImp.UserDaoImp"%>

<%
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}
	if (!"Delivery agent".equals(user.getRole())) {
		response.sendRedirect("Restaurant.jsp");
		return;
	}

	List<Order> activeOrders = (List<Order>) request.getAttribute("activeOrders");
	if (activeOrders == null) {
		activeOrders = new java.util.ArrayList<Order>();
	}

	RestaurantDaoImp restaurantDao = new RestaurantDaoImp();
	OrderItemDaoImp orderItemDao = new OrderItemDaoImp();
	MenuDaoImp menuDao = new MenuDaoImp();
	UserDaoImp userDao = new UserDaoImp();

	int readyForPickupCount = 0;
	int outForDeliveryCount = 0;
	for (Order o : activeOrders) {
		if ("Preparing".equalsIgnoreCase(o.getStatus())) readyForPickupCount++;
		else if ("Out for Delivery".equalsIgnoreCase(o.getStatus())) outForDeliveryCount++;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Delivery dashboard — Foodie Express</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Fraunces:wght@500;600;700&family=Work+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tabler-icons/2.44.0/iconfont/tabler-icons.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
	<div class="container" style="display:flex; align-items:center; justify-content:space-between;">
		<a href="deliveryDashboardServlet" class="logo">Foodie<span>Express</span></a>
		<div class="nav-user">
			<span class="greeting">Hi, <b><%= user.getUserName() %></b> 👋 (Delivery)</span>
			<a href="LogoutServlet" class="btn btn-ghost" style="padding:8px 16px; font-size:13px;">Log out</a>
		</div>
	</div>
</nav>

<header class="admin-hero">
	<div class="container">
		<h1>Delivery queue</h1>
		<p><%= readyForPickupCount %> ready for pickup · <%= outForDeliveryCount %> out for delivery</p>
	</div>
</header>

<main class="container" style="padding-top:24px; padding-bottom:60px;">

	<% if (activeOrders.isEmpty()) { %>

		<div class="empty-cart">
			<i class="ti ti-motorbike"></i>
			<h2>You're all caught up</h2>
			<p>No orders are ready for pickup or out for delivery right now.</p>
		</div>

	<% } else {
		for (Order order : activeOrders) {

			Restaurant restaurant = restaurantDao.getRestaurant(order.getRestaurantId());
			User customer = userDao.getUser(order.getUserId());
			List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(order.getOrderId());
			boolean isPreparing = "Preparing".equalsIgnoreCase(order.getStatus());
	%>
	<div class="order-card" style="padding:18px 20px;">

		<div style="display:flex; align-items:center; justify-content:space-between; gap:16px;">
			<h3 style="font-size:15px; font-weight:600;">Order #<%= order.getOrderId() %></h3>
			<span class="status-pill <%= isPreparing ? "progress" : "open" %>">
				<span class="dot"></span> <%= order.getStatus() %>
			</span>
		</div>

		<div style="display:flex; gap:24px; margin-top:14px; flex-wrap:wrap;">
			<div style="flex:1; min-width:200px;">
				<div style="font-size:11px; font-weight:700; text-transform:uppercase; letter-spacing:0.4px; color:rgba(42,33,24,0.4);">Pickup from</div>
				<div style="font-size:14px; font-weight:600; margin-top:4px;"><%= restaurant != null ? restaurant.getRestaurantName() : "Restaurant" %></div>
				<div style="font-size:13px; color:rgba(42,33,24,0.6); margin-top:2px;"><%= restaurant != null ? restaurant.getAddress() : "" %></div>
			</div>
			<div style="flex:1; min-width:200px;">
				<div style="font-size:11px; font-weight:700; text-transform:uppercase; letter-spacing:0.4px; color:rgba(42,33,24,0.4);">Deliver to</div>
				<div style="font-size:14px; font-weight:600; margin-top:4px;"><%= customer != null ? customer.getUserName() : "Customer" %></div>
				<div style="font-size:13px; color:rgba(42,33,24,0.6); margin-top:2px;"><%= customer != null ? customer.getAddress() : "" %></div>
			</div>
		</div>

		<details style="margin-top:12px;">
			<summary style="cursor:pointer; font-size:13px; font-weight:600; color:var(--chili); list-style:none;">
				<%= orderItems.size() %> item<%= orderItems.size() == 1 ? "" : "s" %> · ₹<%= (int) order.getTotalAmount() %> · view items
			</summary>
			<div style="margin-top:10px; padding-top:10px; border-top:1px dashed var(--line-strong);">
				<%
					for (OrderItem oi : orderItems) {
						Menu menu = menuDao.getMenu(oi.getMenuId());
						String itemName = (menu != null) ? menu.getItemName() : "Item";
				%>
				<div class="order-detail-row">
					<span class="item-name"><%= itemName %> × <%= oi.getQuantity() %></span>
					<span>₹<%= (int) oi.getItemTotal() %></span>
				</div>
				<% } %>
			</div>
		</details>

		<form class="order-status-form" action="deliveryUpdateStatusServlet" method="post">
			<input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
			<% if (isPreparing) { %>
				<input type="hidden" name="status" value="Out for Delivery">
				<button type="submit" class="btn btn-primary btn-block">
					<i class="ti ti-motorbike"></i> Picked up — mark Out for Delivery
				</button>
			<% } else { %>
				<input type="hidden" name="status" value="Delivered">
				<button type="submit" class="btn btn-primary btn-block">
					<i class="ti ti-circle-check"></i> Mark Delivered
				</button>
			<% } %>
		</form>
	</div>
	<% } } %>

</main>

<script src="js/app.js"></script>

</body>
</html>
