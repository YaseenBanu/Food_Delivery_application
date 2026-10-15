package com.tap.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.tap.DaoImp.MenuDaoImp;
import com.tap.DaoImp.RestaurantDaoImp;
import com.tap.model.Menu;
import com.tap.model.Restaurant;
import com.tap.model.User;

@WebServlet("/saveMenuServlet")
public class SaveMenuServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

		if (user == null || !"Admin".equals(user.getRole())) {
			response.sendRedirect("login.html");
			return;
		}

		// Security check: confirm this admin actually owns the restaurant
		// they're trying to add/edit a menu item for — without this, any
		// Admin could edit ANY restaurant's menu just by changing the
		// restaurantId in the form.
		Restaurant restaurant = new RestaurantDaoImp().getRestaurantByAdminId(user.getUserId());
		if (restaurant == null) {
			response.sendRedirect("Restaurant.jsp");
			return;
		}

		String menuIdParam = request.getParameter("menuId");
		String itemName = request.getParameter("itemName");
		String description = request.getParameter("description");
		float price = Float.parseFloat(request.getParameter("price"));
		boolean isAvailable = "on".equals(request.getParameter("isAvailable"));
		String category = request.getParameter("category");
		String imagePath = request.getParameter("imagePath");

		MenuDaoImp menuDao = new MenuDaoImp();

		if (menuIdParam == null || menuIdParam.trim().isEmpty()) {
			// No menuId -> this is a new item.
			Menu menu = new Menu(restaurant.getRestaurantId(), itemName, description, price, isAvailable, category);
			menu.setImagePath(imagePath);
			menuDao.addMenu(menu);
		} else {
			// menuId present -> this is an edit of an existing item.
			int menuId = Integer.parseInt(menuIdParam);
			Menu menu = new Menu(menuId, restaurant.getRestaurantId(), itemName, description, price, isAvailable, category, imagePath);
			menuDao.updateMenu(menu);
		}

		response.sendRedirect("adminDashboardServlet");
	}
}