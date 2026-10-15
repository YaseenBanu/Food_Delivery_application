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

@WebServlet("/deleteMenuServlet")
public class DeleteMenuServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

		int menuId = Integer.parseInt(request.getParameter("menuId"));
		MenuDaoImp menuDao = new MenuDaoImp();

		// Security check: only allow deleting items that actually belong to
		// THIS admin's restaurant — stops one Admin from deleting another
		// restaurant's menu items by guessing menuId values in the URL.
		Menu menu = menuDao.getMenu(menuId);
		if (menu != null && menu.getRestaurantId() == restaurant.getRestaurantId()) {
			menuDao.deleteMenu(menuId);
		}

		response.sendRedirect("adminDashboardServlet");
	}
}