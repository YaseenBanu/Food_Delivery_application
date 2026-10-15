package com.tap.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.OrderDaoImp;
import com.tap.model.Order;
import com.tap.model.User;

@WebServlet("/deliveryDashboardServlet")
public class DeliveryDashboardServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		if (user == null) {
			response.sendRedirect("login.html");
			return;
		}
		if (!"Delivery agent".equals(user.getRole())) {
			response.sendRedirect("Restaurant.jsp");
			return;
		}

		OrderDaoImp orderDao = new OrderDaoImp();
		List<Order> activeOrders = orderDao.getActiveOrdersForDelivery();

		request.setAttribute("activeOrders", activeOrders);
		request.getRequestDispatcher("deliveryDashboard.jsp").forward(request, response);
	}
}