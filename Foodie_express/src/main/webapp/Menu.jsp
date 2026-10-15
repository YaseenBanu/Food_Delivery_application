<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="com.tap.model.Menu"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>

<%
	// Guard: no session -> back to login, same pattern as Restaurant.jsp
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}

	Restaurant restaurant = (Restaurant) request.getAttribute("restaurant");
	if (restaurant == null) {
		// Invalid or missing restaurantId — don't crash, just send them back.
		response.sendRedirect("callRestaurantServlet");
		return;
	}

	List<Menu> menuItems = (List<Menu>) request.getAttribute("allMenu");
	if (menuItems == null) {
		menuItems = new java.util.ArrayList<Menu>();
	}

	// Distinct categories, in first-seen order, to drive the filter chips.
	LinkedHashSet<String> categories = new LinkedHashSet<String>();
	for (Menu m : menuItems) {
		categories.add(m.getCategory());
	}

	// Unsplash's old "Source" endpoint (source.unsplash.com) was shut down —
	// any menu item using it will 404 on image load, so we fall back to this
	// generic food photo via the onerror handler on each <img> below.
	String fallbackImage = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><%= restaurant.getRestaurantName() %> — Foodie Express</title>
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
			<a href="cart.jsp" class="icon-link">
				<i class="ti ti-shopping-cart"></i>
				<span class="dot"></span>
			</a>
		</div>
	</div>
</nav>

<header class="restaurant-banner">
	<div class="banner-image" style="background-image:url('<%= restaurant.getImagePath() %>')"></div>
	<div class="container banner-info">
		<a href="Restaurant.jsp" class="back-link"><i class="ti ti-arrow-left"></i> All restaurants</a>
		<h1><%= restaurant.getRestaurantName() %></h1>
		<div class="meta-row">
			<span><i class="ti ti-star-filled"></i> <%= restaurant.getRating() %></span>
			<span><i class="ti ti-clock"></i> <%= restaurant.getDeliveryTime() %> mins</span>
			<span><i class="ti ti-map-pin"></i> <%= restaurant.getAddress() %></span>
			<span><i class="ti ti-toolsKitchen2"></i> <%= restaurant.getCuisineType() %></span>
			<% if (restaurant.isActive()) { %>
				<span class="status-pill open" style="margin-top:0;"><span class="dot"></span> Open now</span>
			<% } else { %>
				<span class="status-pill closed" style="margin-top:0;"><span class="dot"></span> Closed</span>
			<% } %>
		</div>
	</div>
</header>

<main class="container">

	<div class="chip-row menu-chip-row" id="categoryChips">
		<div class="chip active" data-cat="all"><i class="ti ti-apps"></i> All</div>
		<%
			for (String cat : categories) {
		%>
		<div class="chip" data-cat="<%= cat.toLowerCase() %>"><%= cat %></div>
		<% } %>
	</div>

	<section class="section" style="padding-top:12px;">
		<div class="menu-list" id="menuList">
			<%
				if (menuItems.isEmpty()) {
			%>
			<div class="no-results" style="display:block;">
				<i class="ti ti-toolsKitchen-2"></i>
				This restaurant hasn't added any menu items yet.
			</div>
			<%
				} else {
					for (Menu item : menuItems) {
			%>
			<div class="menu-item<%= item.isAvailable() ? "" : " unavailable" %>" data-cat="<%= item.getCategory().toLowerCase() %>">
				<div class="thumb-sm" style="background-image:url('<%= item.getImagePath() %>')" data-fallback="<%= fallbackImage %>"></div>
				<div class="info">
					<span class="cat-tag"><%= item.getCategory() %></span>
					<h3><%= item.getItemName() %></h3>
					<p><%= item.getDescription() %></p>
				</div>
				<div class="side">
					<span class="price">₹<%= (int) item.getPrice() %></span>
					<% if (item.isAvailable()) { %>
						<a class="btn btn-primary add-btn"
							href="CartServlet?action=add&menuId=<%= item.getMenuId() %>&quantity=1&restaurantId=<%= restaurant.getRestaurantId() %>">
							Add
						</a>
					<% } else { %>
						<button class="btn btn-primary add-btn" disabled>Sold out</button>
					<% } %>
				</div>
			</div>
			<%
					}
				}
			%>
		</div>

		<div class="no-results" id="noResults">
			<i class="ti ti-toolsKitchen-2"></i>
			No items in this category.
		</div>
	</section>

</main>

<nav class="bottom-nav">
	<a href="Restaurant.jsp"><i class="ti ti-home"></i> Home</a>
	<a href="cart.jsp"><i class="ti ti-shopping-cart"></i> Cart</a>
	<a href="orders.jsp"><i class="ti ti-receipt"></i> Orders</a>
	<a href="login.html"><i class="ti ti-user"></i> Account</a>
</nav>

<div id="toast-stack"></div>

<script src="js/app.js"></script>
<script>
	// Broken/dead image URLs (e.g. old source.unsplash.com links) fall back
	// to a generic food photo instead of showing a broken-image icon.
	document.querySelectorAll(".thumb-sm").forEach(function (el) {
		var img = new Image();
		img.onerror = function () {
			el.style.backgroundImage = "url('" + el.dataset.fallback + "')";
		};
		img.src = el.style.backgroundImage.slice(5, -2); // strip url("...")
	});

	// Category chip filtering
	document.querySelectorAll("#categoryChips .chip").forEach(function (chip) {
		chip.addEventListener("click", function () {
			document.querySelectorAll("#categoryChips .chip").forEach(function (c) { c.classList.remove("active"); });
			chip.classList.add("active");

			var cat = chip.dataset.cat;
			var items = document.querySelectorAll("#menuList .menu-item");
			var visible = 0;

			items.forEach(function (item) {
				var match = (cat === "all") || (item.dataset.cat === cat);
				item.style.display = match ? "" : "none";
				if (match) visible++;
			});

			document.getElementById("noResults").style.display = visible === 0 ? "block" : "none";
		});
	});
</script>

</body>
</html>
