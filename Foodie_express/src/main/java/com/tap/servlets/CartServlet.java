package com.tap.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.tap.DaoImp.MenuDaoImp;
import com.tap.model.Cart;
import com.tap.model.CartItems;
import com.tap.model.Menu;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");

		String action = request.getParameter("action");
		// FIX: guard against a missing/blank action param instead of
		// letting action.equals(...) throw an NPE.
		if (action == null) {
			action = "";
		}

		// FIX: only require + parse restaurantId when we're actually adding
		// an item. update/delete never send this param, so parsing it
		// unconditionally caused a NumberFormatException (500 error) on
		// every quantity change or item removal.
		if (action.equals("add")) {
			int newRestaurantId = Integer.parseInt(request.getParameter("restaurantId"));
			Integer restaurantId = (Integer) session.getAttribute("restaurantId");

			if (cart == null || restaurantId == null || restaurantId != newRestaurantId) {
				// Starting a cart for a new/different restaurant clears any
				// existing cart — same behavior you already had, just moved
				// inside the "add" branch where restaurantId is actually known.
				cart = new Cart();
				session.setAttribute("cart", cart);
				session.setAttribute("restaurantId", newRestaurantId);
			}
			addToCartItem(request, cart);

		} else if (action.equals("update")) {
			if (cart != null) {
				updateCartItem(request, cart);
			}

		} else if (action.equals("delete")) {
			if (cart != null) {
				deleteCartItem(request, cart);
			}
		}

		response.sendRedirect("cart.jsp");
	}

	private void deleteCartItem(HttpServletRequest request, Cart cart) {
		int menuId = Integer.parseInt(request.getParameter("menuId"));
		cart.removeItem(menuId);
	}

	private void updateCartItem(HttpServletRequest request, Cart cart) {
		int menuId = Integer.parseInt(request.getParameter("menuId"));
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		cart.updateItem(menuId, quantity);
	}

	private void addToCartItem(HttpServletRequest request, Cart cart) {
		int menuId = Integer.parseInt(request.getParameter("menuId"));
		int quantity = Integer.parseInt(request.getParameter("quantity"));

		MenuDaoImp mdo = new MenuDaoImp();
		Menu menu = mdo.getMenu(menuId);

		// FIX: null-check BEFORE touching menu.getRestaurantId() — previously
		// this line ran unconditionally above the null check and would NPE
		// if menuId didn't match any row.
		if (menu != null) {
			HttpSession session = request.getSession();
			session.setAttribute("restaurantId", menu.getRestaurantId());

			CartItems cartItem = new CartItems(
					menuId, menu.getRestaurantId(), menu.getItemName(), menu.getPrice(), quantity);
			cart.addItem(cartItem);
		}
	}
}