package com.tap.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.OrderDaoImp;
import com.tap.DaoImp.RestaurantDaoImp;
import com.tap.model.Order;
import com.tap.model.Restaurant;
import com.tap.model.User;

@WebServlet("/updateOrderStatusServlet")
public class UpdateOrderStatusServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		if (user == null || !"Admin".equals(user.getRole())) {
			response.sendRedirect("login.html");
			return;
		}

		Restaurant restaurant = new RestaurantDaoImp().getRestaurantByAdminId(user.getUserId());
		if (restaurant == null) {
			response.sendRedirect("Restaurant.jsp");
			return;
		}

		int orderId = Integer.parseInt(request.getParameter("orderId"));
		String status = request.getParameter("status");

		OrderDaoImp orderDao = new OrderDaoImp();

		// Security check: only allow updating orders that actually belong to
		// THIS admin's restaurant.
		Order order = orderDao.getOrder(orderId);
		if (order != null && order.getRestaurantId() == restaurant.getRestaurantId()) {
			orderDao.updateOrderStatus(orderId, status);
		}

		response.sendRedirect("adminDashboardServlet");
	}
}