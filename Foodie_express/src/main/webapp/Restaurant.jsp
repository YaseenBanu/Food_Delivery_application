<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.tap.model.Restaurant"%>
<%@ page import="com.tap.model.User"%>

<%
	// Guard: if nobody is logged in (direct hit, session timeout, back button),
	// bounce to login instead of letting user.getUserName() throw an NPE below.
	User user = (User) session.getAttribute("loggedInUser");
	if (user == null) {
		response.sendRedirect("login.html");
		return;
	}

	List<Restaurant> restaurants =
			(List<Restaurant>) request.getAttribute("allRestaurants");
	if (restaurants == null) {
		restaurants = new java.util.ArrayList<Restaurant>();
	}

	String[] offers = {
			"30% OFF", "FREE DELIVERY", "₹100 OFF", "BUY 1 GET 1", "20% OFF"
	};
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Foodie Express — Restaurants near you</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Fraunces:wght@500;600;700&family=Work+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tabler-icons/2.44.0/iconfont/tabler-icons.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
	<div class="container" style="display:flex; align-items:center;">
		<a href="Restaurant.jsp" class="logo">Foodie<span>Express</span></a>

		<div class="nav-search">
			<i class="ti ti-search"></i>
			<input type="text" id="searchInput" placeholder="Search restaurants or cuisines..." onkeyup="filterRestaurants()" />
		</div>

		<div class="nav-user">
			<span class="greeting">Hi, <b><%= user.getUserName() %></b> 👋</span>
			<a href="cart.jsp" class="icon-link">
				<i class="ti ti-shopping-cart"></i>
				<span class="dot"></span>
			</a>
			<a href="LogoutServlet" class="btn btn-ghost" style="padding:8px 16px; font-size:13px;">Log out</a>
		</div>
	</div>
</nav>

<header class="dash-hero">
	<div class="container">
		<h1>Good choice today, <%= user.getUserName() %>.</h1>
		<p>Here's what opens and delivering fast in your area right now.</p>
	</div>
</header>

<main class="container">
	<section class="section" id="restaurants">
		<div class="section-head">
			<h2>Restaurants near you</h2>
			<span style="font-size:13px; color:rgba(42,33,24,0.5);"><%= restaurants.size() %> found</span>
		</div>

		<div class="card-grid" id="restaurantContainer">
			<%
				int index = 0;
				for (Restaurant restaurant : restaurants) {
					String offer = offers[index % offers.length];
					index++;
			%>
			<a class="ticket-card" data-name="<%= restaurant.getRestaurantName().toLowerCase() %>"
			   data-cuisine="<%= restaurant.getCuisineType().toLowerCase() %>"
			   href="callMenuServlet?restaurantId=<%= restaurant.getRestaurantId() %>">

				<div class="thumb" style="background-image:url('<%= restaurant.getImagePath() %>')">
					<span class="badge promo"><%= offer %></span>
				</div>
				<div class="ticket-perf"></div>
				<div class="ticket-body">
					<h3><%= restaurant.getRestaurantName() %></h3>
					<div class="ticket-meta">
						<span class="rating"><i class="ti ti-star-filled"></i> <%= restaurant.getRating() %></span>
						<span>·</span>
						<span><%= restaurant.getDeliveryTime() %> mins</span>
					</div>
					<div class="ticket-tags"><%= restaurant.getCuisineType() %> · <%= restaurant.getAddress() %></div>

					<% if (restaurant.isActive()) { %>
						<div class="status-pill open"><span class="dot"></span> Open now</div>
					<% } else { %>
						<div class="status-pill closed"><span class="dot"></span> Closed</div>
					<% } %>
				</div>
			</a>
			<% } %>
		</div>

		<div class="no-results" id="noResults">
			<i class="ti ti-toolsKitchen-2"></i>
			No restaurants match your search.
		</div>
	</section>
</main>

<nav class="bottom-nav">
	<a href="Restaurant.jsp" class="active"><i class="ti ti-home"></i> Home</a>
	<a href="cart.jsp"><i class="ti ti-shopping-cart"></i> Cart</a>
	<a href="orders.jsp"><i class="ti ti-receipt"></i> Orders</a>
	<a href="login.html"><i class="ti ti-user"></i> Account</a>
</nav>

<div id="toast-stack"></div>

<footer>
	<div class="container">
		Foodie Express — delivering happiness, one meal at a time. Built with Java, JSP, Servlets, JDBC &amp; MySQL.
	</div>
</footer>

<script src="js/app.js"></script>
<script>
	// Client-side filter over the already-rendered cards — no extra servlet
	// round-trip needed for a simple name/cuisine search.
	function filterRestaurants() {
		var query = document.getElementById("searchInput").value.trim().toLowerCase();
		var cards = document.querySelectorAll("#restaurantContainer .ticket-card");
		var visibleCount = 0;

		cards.forEach(function (card) {
			var matches = card.dataset.name.includes(query) || card.dataset.cuisine.includes(query);
			card.style.display = matches ? "" : "none";
			if (matches) visibleCount++;
		});

		document.getElementById("noResults").style.display = visibleCount === 0 ? "block" : "none";
	}
</script>

</body>
</html>
