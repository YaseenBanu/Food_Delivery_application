package com.tap.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.OrderDaoImp;
import com.tap.DaoImp.OrderItemDaoImp;
import com.tap.model.Cart;
import com.tap.model.CartItems;
import com.tap.model.Order;
import com.tap.model.OrderItem;
import com.tap.model.User;

@WebServlet("/PlaceOrderServlet")
public class PlaceOrderServlet extends HttpServlet {

	// Keep these in sync with the same numbers used in cart.jsp's order
	// summary, so what the customer sees before paying matches what's
	// actually saved to the order.
	private static final double DELIVERY_FEE = 40;
	private static final double TAX_RATE = 0.05;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		if (user == null) {
			response.sendRedirect("login.html");
			return;
		}

		Cart cart = (Cart) session.getAttribute("cart");
		Integer restaurantId = (Integer) session.getAttribute("restaurantId");

		// Guard: nothing to order, or someone POSTs here directly without a cart.
		if (cart == null || cart.getItems().isEmpty() || restaurantId == null) {
			response.sendRedirect("cart.jsp");
			return;
		}

		String paymentMethod = request.getParameter("paymentMethod");
		if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
			paymentMethod = "Cash on Delivery";
		}

		double subtotal = cart.getSubtotal();
		double taxes = Math.round(subtotal * TAX_RATE * 100) / 100.0;
		double grandTotal = subtotal + DELIVERY_FEE + taxes;

		// 1) Create the order first, so we get a real orderId back.
		//    Status starts as "Placed" — CHECK this matches your `status`
		//    column if it's an ENUM (same issue as the `role` column earlier).
		Order order = new Order(
				user.getUserId(),
				restaurantId,
				new Timestamp(System.currentTimeMillis()),
				grandTotal,
				"Placed",
				paymentMethod
		);

		OrderDaoImp orderDao = new OrderDaoImp();
		int orderId = orderDao.addOrder(order);

		if (orderId <= 0) {
			// Insert failed (check server console for the real SQLException) —
			// send the customer back to their cart instead of a broken confirmation.
			request.setAttribute("errorMessage", "We couldn't place your order. Please try again.");
			request.getRequestDispatcher("cart.jsp").forward(request, response);
			return;
		}

		// 2) One OrderItem row per cart line, all tied to that new orderId.
		OrderItemDaoImp orderItemDao = new OrderItemDaoImp();
		for (Map.Entry<Integer, CartItems> entry : cart.getItems().entrySet()) {
			CartItems item = entry.getValue();
			double itemTotal = item.getPrice() * item.getQuantity();

			OrderItem orderItem = new OrderItem(orderId, item.getMenuId(), item.getQuantity(), itemTotal);
			orderItemDao.addOrderItem(orderItem);
		}

		// 3) Order is safely in the DB — now it's safe to clear the cart.
		session.removeAttribute("cart");
		session.removeAttribute("restaurantId");

		// 4) Show confirmation.
		response.sendRedirect("orderConfirmation.jsp?orderId=" + orderId);
	}
}