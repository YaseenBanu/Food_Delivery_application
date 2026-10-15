<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="com.tap.model.Cart"%>
<%@ page import="com.tap.model.CartItems"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.DaoImp.RestaurantDaoImp"%>

<%
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}

	Cart cart = (Cart) session.getAttribute("cart");
	Integer restaurantId = (Integer) session.getAttribute("restaurantId");

	boolean isEmpty = (cart == null || cart.getItems().isEmpty());

	Restaurant restaurant = null;
	if (!isEmpty && restaurantId != null) {
		restaurant = new RestaurantDaoImp().getRestaurant(restaurantId);
	}

	double subtotal = isEmpty ? 0 : cart.getSubtotal();
	double deliveryFee = isEmpty ? 0 : 40;
	double taxes = isEmpty ? 0 : Math.round(subtotal * 0.05 * 100) / 100.0;
	double grandTotal = subtotal + deliveryFee + taxes;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Your cart — Foodie Express</title>
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

<main class="container" style="padding-top:28px; padding-bottom:60px;">

	<a href="<%= restaurant != null ? "callMenuServlet?restaurantId=" + restaurant.getRestaurantId() : "Restaurant.jsp" %>" class="back-link" style="color:rgba(42,33,24,0.6); padding-top:0;">
		<i class="ti ti-arrow-left"></i> <%= restaurant != null ? "Back to menu" : "Back to restaurants" %>
	</a>

	<h1 class="display" style="font-size:28px; margin-top:14px; margin-bottom:6px;">Your cart</h1>

	<% if (isEmpty) { %>

		<div class="empty-cart">
			<i class="ti ti-shopping-cart-off"></i>
			<h2>Your cart is empty</h2>
			<p>Looks like you haven't added anything yet. Let's fix that.</p>
			<a href="Restaurant.jsp" class="btn btn-primary">Browse restaurants</a>
		</div>

	<% } else { %>

		<% if (restaurant != null) { %>
		<div class="cart-restaurant-tag">
			<i class="ti ti-toolsKitchen2"></i>
			Ordering from <b><%= restaurant.getRestaurantName() %></b>
		</div>
		<% } %>

		<div class="cart-layout">

			<div class="cart-items">
				<%
					Map<Integer, CartItems> items = cart.getItems();
					for (CartItems item : items.values()) {
						double lineTotal = item.getPrice() * item.getQuantity();
				%>
				<div class="cart-row">
					<div class="row-info">
						<h3><%= item.getName() %></h3>
						<div class="unit-price">₹<%= (int) item.getPrice() %> each</div>
					</div>

					<div class="qty-stepper">
						<a href="CartServlet?action=update&menuId=<%= item.getMenuId() %>&quantity=<%= item.getQuantity() - 1 %>">−</a>
						<span><%= item.getQuantity() %></span>
						<a href="CartServlet?action=update&menuId=<%= item.getMenuId() %>&quantity=<%= item.getQuantity() + 1 %>">+</a>
					</div>

					<div class="line-total">₹<%= (int) lineTotal %></div>

					<a class="remove-link" href="CartServlet?action=delete&menuId=<%= item.getMenuId() %>" title="Remove item">
						<i class="ti ti-trash"></i>
					</a>
				</div>
				<% } %>

				<% if (restaurant != null) { %>
				<a href="callMenuServlet?restaurantId=<%= restaurant.getRestaurantId() %>"
					class="btn btn-outline" style="margin-top:4px; width:100%; justify-content:center;">
					<i class="ti ti-plus"></i> Add more items from <%= restaurant.getRestaurantName() %>
				</a>
				<% } %>
			</div>

			<div class="order-summary">
				<h3>Order summary</h3>
				<div class="summary-line"><span>Subtotal (<%= cart.getTotalItemCount() %> items)</span><span>₹<%= (int) subtotal %></span></div>
				<div class="summary-line"><span>Delivery fee</span><span>₹<%= (int) deliveryFee %></span></div>
				<div class="summary-line"><span>Taxes &amp; charges</span><span>₹<%= (int) taxes %></span></div>
				<div class="summary-line total"><span>To pay</span><span>₹<%= (int) grandTotal %></span></div>

				<form action="PlaceOrderServlet" method="post">
					<div class="field-group" style="margin-top:16px; margin-bottom:4px;">
						<label for="paymentMethod">Payment method</label>
						<select id="paymentMethod" name="paymentMethod" required>
							<option value="Cash on Delivery">Cash on Delivery</option>
							<option value="Card">Card</option>
							<option value="UPI">UPI</option>
						</select>
					</div>
					<button type="submit" class="btn btn-primary btn-block" style="margin-top:14px;">
						Place order · ₹<%= (int) grandTotal %>
					</button>
				</form>
			</div>

		</div>

	<% } %>

</main>

<div id="toast-stack"></div>

<nav class="bottom-nav">
	<a href="Restaurant.jsp"><i class="ti ti-home"></i> Home</a>
	<a href="cart.jsp" class="active"><i class="ti ti-shopping-cart"></i> Cart</a>
	<a href="orders.jsp"><i class="ti ti-receipt"></i> Orders</a>
	<a href="login.html"><i class="ti ti-user"></i> Account</a>
</nav>

<script src="js/app.js"></script>

</body>
</html>
