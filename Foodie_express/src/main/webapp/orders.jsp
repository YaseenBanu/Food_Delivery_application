<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.tap.model.Order"%>
<%@ page import="com.tap.model.OrderItem"%>
<%@ page import="com.tap.model.Menu"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.DaoImp.OrderDaoImp"%>
<%@ page import="com.tap.DaoImp.OrderItemDaoImp"%>
<%@ page import="com.tap.DaoImp.MenuDaoImp"%>
<%@ page import="com.tap.DaoImp.RestaurantDaoImp"%>

<%
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}

	OrderDaoImp orderDao = new OrderDaoImp();
	OrderItemDaoImp orderItemDao = new OrderItemDaoImp();
	RestaurantDaoImp restaurantDao = new RestaurantDaoImp();
	MenuDaoImp menuDao = new MenuDaoImp();

	List<Order> orders = orderDao.getOrdersByUser(user.getUserId());
	boolean isEmpty = (orders == null || orders.isEmpty());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Your orders — Foodie Express</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Fraunces:wght@500;600;700&family=Work+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tabler-icons/2.44.0/iconfont/tabler-icons.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
	<div class="container" style="display:flex; align-items:center; justify-content:space-between;">
		<a href="Restaurant.jsp" class="logo">Foodie<span>Express</span></a>
		<div class="nav-user">
			<span class="greeting">Hi, <b><%= user.getUserName() %></b> 👋</span>
		</div>
	</div>
</nav>

<main class="container" style="max-width:720px; padding-top:28px; padding-bottom:60px;">

	<h1 class="display" style="font-size:28px; margin-bottom:20px;">Your orders</h1>

	<% if (isEmpty) { %>

		<div class="empty-cart">
			<i class="ti ti-receipt-off"></i>
			<h2>No orders yet</h2>
			<p>Once you place an order, it'll show up here.</p>
			<a href="Restaurant.jsp" class="btn btn-primary">Browse restaurants</a>
		</div>

	<% } else {
		for (Order order : orders) {

			Restaurant restaurant = restaurantDao.getRestaurant(order.getRestaurantId());
			List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(order.getOrderId());

			String statusClass;
			String status = order.getStatus();
			if ("Delivered".equalsIgnoreCase(status)) {
				statusClass = "open";
			} else if ("Cancelled".equalsIgnoreCase(status)) {
				statusClass = "cancelled";
			} else {
				statusClass = "progress";
			}
	%>
	<details class="order-card">
		<summary>
			<div class="order-head-left">
				<h3><%= restaurant != null ? restaurant.getRestaurantName() : "Restaurant" %></h3>
				<div class="order-meta">
					Order #<%= order.getOrderId() %> · <%= order.getOrderDate() %>
				</div>
			</div>
			<div class="order-head-right">
				<span class="status-pill <%= statusClass %>"><span class="dot"></span> <%= order.getStatus() %></span>
				<span class="order-total">₹<%= (int) order.getTotalAmount() %></span>
				<i class="ti ti-chevron-down chevron"></i>
			</div>
		</summary>

		<div class="order-detail">
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

			<div class="order-detail-row" style="border-top:1px dashed var(--line-strong); margin-top:8px; padding-top:12px; font-weight:600; color:var(--ink);">
				<span>Paid via <%= order.getPaymentMethod() %></span>
				<span>₹<%= (int) order.getTotalAmount() %></span>
			</div>

			<% if (restaurant != null) { %>
			<a href="callMenuServlet?restaurantId=<%= restaurant.getRestaurantId() %>"
				class="btn btn-outline" style="margin-top:16px; width:100%; justify-content:center;">
				Order again from <%= restaurant.getRestaurantName() %>
			</a>
			<% } %>
		</div>
	</details>
	<% } } %>

</main>

<nav class="bottom-nav">
	<a href="Restaurant.jsp"><i class="ti ti-home"></i> Home</a>
	<a href="cart.jsp"><i class="ti ti-shopping-cart"></i> Cart</a>
	<a href="orders.jsp" class="active"><i class="ti ti-receipt"></i> Orders</a>
	<a href="login.html"><i class="ti ti-user"></i> Account</a>
</nav>

<script src="js/app.js"></script>

</body>
</html>
