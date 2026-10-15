package com.tap.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.MenuDaoImp;
import com.tap.DaoImp.OrderDaoImp;
import com.tap.DaoImp.RestaurantDaoImp;
import com.tap.model.Menu;
import com.tap.model.Order;
import com.tap.model.Restaurant;
import com.tap.model.User;

@WebServlet("/adminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		// Guard: must be logged in AND actually be an Admin. Without the role
		// check, any logged-in customer could hit this URL directly and see
		// someone else's restaurant management screen.
		if (user == null) {
			response.sendRedirect("login.html");
			return;
		}
		if (!"Admin".equals(user.getRole())) {
			response.sendRedirect("Restaurant.jsp");
			return;
		}

		RestaurantDaoImp restaurantDao = new RestaurantDaoImp();
		Restaurant restaurant = restaurantDao.getRestaurantByAdminId(user.getUserId());

		if (restaurant == null) {
			// This admin account isn't linked to any restaurant yet
			// (restaurant.adminUserId must be set manually in the DB for now —
			// there's no self-service "create my restaurant" flow built yet).
			request.setAttribute("noRestaurant", true);
			request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
			return;
		}

		MenuDaoImp menuDao = new MenuDaoImp();
		OrderDaoImp orderDao = new OrderDaoImp();

		List<Menu> menuItems = menuDao.getMenuByResId(restaurant.getRestaurantId());
		List<Order> orders = orderDao.getOrdersByRestaurant(restaurant.getRestaurantId());

		request.setAttribute("restaurant", restaurant);
		request.setAttribute("menuItems", menuItems);
		request.setAttribute("orders", orders);

		request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
	}
}