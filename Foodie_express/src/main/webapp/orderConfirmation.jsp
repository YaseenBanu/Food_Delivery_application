<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="com.tap.model.Order"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.DaoImp.OrderDaoImp"%>
<%@ page import="com.tap.DaoImp.RestaurantDaoImp"%>

<%
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}

	String orderIdParam = request.getParameter("orderId");
	Order order = null;
	Restaurant restaurant = null;

	if (orderIdParam != null) {
		try {
			int orderId = Integer.parseInt(orderIdParam);
			order = new OrderDaoImp().getOrder(orderId);

			// Make sure this order actually belongs to the logged-in user —
			// without this check, anyone could view any order by guessing
			// ?orderId=N in the URL.
			if (order != null && order.getUserId() == user.getUserId()) {
				restaurant = new RestaurantDaoImp().getRestaurant(order.getRestaurantId());
			} else {
				order = null;
			}
		} catch (NumberFormatException e) {
			order = null;
		}
	}

	if (order == null) {
		response.sendRedirect("Restaurant.jsp");
		return;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Order confirmed — Foodie Express</title>
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

<main class="container" style="max-width:560px; padding-top:70px; padding-bottom:70px; text-align:center;">

	<div style="width:76px; height:76px; border-radius:50%; background:var(--curry-light); display:flex; align-items:center; justify-content:center; margin:0 auto 26px;">
		<i class="ti ti-circle-check" style="font-size:40px; color:var(--curry);"></i>
	</div>

	<h1 class="display" style="font-size:28px;">Order placed!</h1>
	<p style="color:rgba(42,33,24,0.6); margin-top:8px;">
		Your order from <b><%= restaurant != null ? restaurant.getRestaurantName() : "the restaurant" %></b> is confirmed and on its way to being prepared.
	</p>

	<div class="ticket-card" style="margin-top:32px; text-align:left; cursor:default;">
		<div class="ticket-body">
			<div class="ticket-meta" style="justify-content:space-between; margin-top:0;">
				<span>Order #<%= order.getOrderId() %></span>
				<span class="status-pill open"><span class="dot"></span> <%= order.getStatus() %></span>
			</div>
			<h3 style="margin-top:12px;"><%= restaurant != null ? restaurant.getRestaurantName() : "" %></h3>
			<div class="ticket-tags" style="margin-top:8px;">
				Payment: <%= order.getPaymentMethod() %>
			</div>
			<div class="summary-line total" style="margin-top:16px; border-top:1px dashed var(--line-strong); padding-top:12px;">
				<span>Total paid</span><span>₹<%= (int) order.getTotalAmount() %></span>
			</div>
		</div>
	</div>

	<div style="display:flex; gap:12px; margin-top:32px;">
		<a href="Restaurant.jsp" class="btn btn-outline" style="flex:1;">Order more food</a>
		<a href="orders.jsp" class="btn btn-primary" style="flex:1;">View my orders</a>
	</div>

</main>

<script src="js/app.js"></script>

</body>
</html>
