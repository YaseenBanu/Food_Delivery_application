package com.tap.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.OrderDaoImp;
import com.tap.model.User;

@WebServlet("/deliveryUpdateStatusServlet")
public class DeliveryUpdateStatusServlet extends HttpServlet {

	// Delivery agents can only move an order FORWARD through these two
	// states — they shouldn't be able to set "Placed" or "Cancelled",
	// that's the restaurant admin's call.
	private static final List<String> ALLOWED_STATUSES = Arrays.asList("Out for Delivery", "Delivered");

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		if (user == null || !"Delivery agent".equals(user.getRole())) {
			response.sendRedirect("login.html");
			return;
		}

		int orderId = Integer.parseInt(request.getParameter("orderId"));
		String status = request.getParameter("status");

		if (ALLOWED_STATUSES.contains(status)) {
			new OrderDaoImp().updateOrderStatus(orderId, status);
		}

		response.sendRedirect("deliveryDashboardServlet");
	}
}