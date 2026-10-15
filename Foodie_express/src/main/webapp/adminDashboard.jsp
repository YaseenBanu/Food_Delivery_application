<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.tap.model.Menu"%>
<%@ page import="com.tap.model.Order"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.DaoImp.UserDaoImp"%>

<%
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}
	if (!"Admin".equals(user.getRole())) {
		response.sendRedirect("Restaurant.jsp");
		return;
	}

	Boolean noRestaurant = (Boolean) request.getAttribute("noRestaurant");
	if (noRestaurant == null) {
		noRestaurant = false;
	}

	Restaurant restaurant = (Restaurant) request.getAttribute("restaurant");
	List<Menu> menuItems = (List<Menu>) request.getAttribute("menuItems");
	List<Order> orders = (List<Order>) request.getAttribute("orders");

	UserDaoImp userDao = new UserDaoImp();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Restaurant dashboard — Foodie Express</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Fraunces:wght@500;600;700&family=Work+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tabler-icons/2.44.0/iconfont/tabler-icons.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
	<div class="container" style="display:flex; align-items:center; justify-content:space-between;">
		<a href="adminDashboardServlet" class="logo">Foodie<span>Express</span></a>
		<div class="nav-user">
			<span class="greeting">Hi, <b><%= user.getUserName() %></b> 👋 (Admin)</span>
			<a href="LogoutServlet" class="btn btn-ghost" style="padding:8px 16px; font-size:13px;">Log out</a>
		</div>
	</div>
</nav>

<% if (noRestaurant) { %>

	<main class="container">
		<div class="no-restaurant-notice">
			<i class="ti ti-building-store"></i>
			<h2>No restaurant linked to your account yet</h2>
			<p style="color:rgba(42,33,24,0.55); margin-top:8px;">
				Ask a super-admin to set <code>restaurant.adminUserId</code> to your userId
				(<b><%= user.getUserId() %></b>) in the database — there's no self-service
				"create my restaurant" flow yet.
			</p>
		</div>
	</main>

<% } else { %>

	<header class="admin-hero">
		<div class="container">
			<h1><%= restaurant.getRestaurantName() %> — Dashboard</h1>
			<p><%= restaurant.getCuisineType() %> · <%= restaurant.getAddress() %> ·
				<%= restaurant.isActive() ? "Currently open" : "Currently closed" %></p>
		</div>
	</header>

	<main class="container" style="padding-bottom:60px;">

		<div class="tab-row">
			<button class="tab-btn active" data-tab="menuTab" onclick="switchTab('menuTab', this)">Menu (<%= menuItems.size() %>)</button>
			<button class="tab-btn" data-tab="ordersTab" onclick="switchTab('ordersTab', this)">Orders (<%= orders.size() %>)</button>
		</div>

		<!-- ================= MENU TAB ================= -->
		<div class="tab-panel active" id="menuTab">

			<button class="btn btn-primary" style="margin-bottom:18px;" onclick="openMenuForm()">
				<i class="ti ti-plus"></i> Add menu item
			</button>

			<form class="menu-form" id="menuForm" action="saveMenuServlet" method="post">
				<h3 style="font-size:16px; margin-bottom:16px;" id="menuFormTitle">Add new item</h3>
				<input type="hidden" name="menuId" id="f-menuId" value="">
				<div class="form-grid">
					<div class="field-group full">
						<label>Item name</label>
						<input type="text" name="itemName" id="f-itemName" required>
					</div>
					<div class="field-group full">
						<label>Description</label>
						<input type="text" name="description" id="f-description" required>
					</div>
					<div class="field-group">
						<label>Price (₹)</label>
						<input type="number" name="price" id="f-price" step="0.01" min="0" required>
					</div>
					<div class="field-group">
						<label>Category</label>
						<input type="text" name="category" id="f-category" placeholder="e.g. Starter, Main Course" required>
					</div>
					<div class="field-group full">
						<label>Image URL</label>
						<input type="text" name="imagePath" id="f-imagePath" placeholder="https://images.unsplash.com/...">
					</div>
					<div class="checkbox-row full">
						<input type="checkbox" name="isAvailable" id="f-isAvailable" checked>
						<label for="f-isAvailable">Available for ordering</label>
					</div>
				</div>
				<div style="display:flex; gap:10px; margin-top:18px;">
					<button type="submit" class="btn btn-primary">Save item</button>
					<button type="button" class="btn btn-outline" onclick="closeMenuForm()">Cancel</button>
				</div>
			</form>

			<%
				if (menuItems.isEmpty()) {
			%>
			<div class="no-results" style="display:block;">
				<i class="ti ti-toolsKitchen-2"></i>
				No menu items yet — add your first one above.
			</div>
			<%
				} else {
					for (Menu item : menuItems) {
			%>
			<div class="admin-item-row">
				<div class="thumb-sm" style="width:56px; height:56px; background-image:url('<%= item.getImagePath() %>')"></div>
				<div class="info">
					<h4><%= item.getItemName() %></h4>
					<div class="meta">₹<%= (int) item.getPrice() %> · <%= item.getCategory() %> · <%= item.isAvailable() ? "Available" : "Sold out" %></div>
				</div>
				<div class="actions">
					<a class="icon-btn" title="Edit" onclick="editMenuItem(
						<%= item.getMenuId() %>,
						'<%= item.getItemName().replace("'", "\\'") %>',
						'<%= item.getDescription().replace("'", "\\'") %>',
						<%= item.getPrice() %>,
						'<%= item.getCategory().replace("'", "\\'") %>',
						'<%= item.getImagePath() != null ? item.getImagePath().replace("'", "\\'") : "" %>',
						<%= item.isAvailable() %>
					)"><i class="ti ti-pencil"></i></a>
					<a class="icon-btn danger" title="Delete" href="deleteMenuServlet?menuId=<%= item.getMenuId() %>"
						onclick="return confirm('Remove this item from your menu?')"><i class="ti ti-trash"></i></a>
				</div>
			</div>
			<% } } %>
		</div>

		<!-- ================= ORDERS TAB ================= -->
		<div class="tab-panel" id="ordersTab">

			<%
				if (orders.isEmpty()) {
			%>
			<div class="no-results" style="display:block;">
				<i class="ti ti-receipt-off"></i>
				No orders yet for your restaurant.
			</div>
			<%
				} else {
					for (Order order : orders) {
						com.tap.model.User customer = userDao.getUser(order.getUserId());
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
			<div class="order-card" style="padding:18px 20px;">
				<div style="display:flex; align-items:center; justify-content:space-between; gap:16px;">
					<div>
						<h3 style="font-size:15px; font-weight:600;">Order #<%= order.getOrderId() %> — <%= customer != null ? customer.getUserName() : "Customer" %></h3>
						<div class="order-meta"><%= order.getOrderDate() %> · <%= order.getPaymentMethod() %></div>
					</div>
					<div style="display:flex; align-items:center; gap:12px;">
						<span class="status-pill <%= statusClass %>"><span class="dot"></span> <%= status %></span>
						<span class="order-total">₹<%= (int) order.getTotalAmount() %></span>
					</div>
				</div>

				<form class="order-status-form" action="updateOrderStatusServlet" method="post">
					<input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
					<select name="status">
						<option value="Placed" <%= "Placed".equalsIgnoreCase(status) ? "selected" : "" %>>Placed</option>
						<option value="Preparing" <%= "Preparing".equalsIgnoreCase(status) ? "selected" : "" %>>Preparing</option>
						<option value="Out for Delivery" <%= "Out for Delivery".equalsIgnoreCase(status) ? "selected" : "" %>>Out for Delivery</option>
						<option value="Delivered" <%= "Delivered".equalsIgnoreCase(status) ? "selected" : "" %>>Delivered</option>
						<option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(status) ? "selected" : "" %>>Cancelled</option>
					</select>
					<button type="submit" class="btn btn-primary">Update</button>
				</form>
			</div>
			<% } } %>
		</div>

	</main>

<% } %>

<script src="js/app.js"></script>
<script>
	function switchTab(tabId, btn) {
		document.querySelectorAll('.tab-panel').forEach(function (p) { p.classList.remove('active'); });
		document.querySelectorAll('.tab-btn').forEach(function (b) { b.classList.remove('active'); });
		document.getElementById(tabId).classList.add('active');
		btn.classList.add('active');
	}

	function openMenuForm() {
		document.getElementById('menuFormTitle').textContent = 'Add new item';
		document.getElementById('menuForm').reset();
		document.getElementById('f-menuId').value = '';
		document.getElementById('menuForm').classList.add('open');
	}

	function closeMenuForm() {
		document.getElementById('menuForm').classList.remove('open');
	}

	function editMenuItem(menuId, itemName, description, price, category, imagePath, isAvailable) {
		document.getElementById('menuFormTitle').textContent = 'Edit item';
		document.getElementById('f-menuId').value = menuId;
		document.getElementById('f-itemName').value = itemName;
		document.getElementById('f-description').value = description;
		document.getElementById('f-price').value = price;
		document.getElementById('f-category').value = category;
		document.getElementById('f-imagePath').value = imagePath;
		document.getElementById('f-isAvailable').checked = isAvailable;
		document.getElementById('menuForm').classList.add('open');
		document.getElementById('menuForm').scrollIntoView({ behavior: 'smooth', block: 'start' });
	}
</script>

</body>
</html>
