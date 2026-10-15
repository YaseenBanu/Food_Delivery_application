package com.tap.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.tap.DaoImp.MenuDaoImp;
import com.tap.DaoImp.RestaurantDaoImp;
import com.tap.model.Menu;
import com.tap.model.Restaurant;

@WebServlet("/callMenuServlet")
public class MenuServlet extends HttpServlet {
       
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
throws ServletException, IOException {

int restaurantId =Integer.parseInt(request.getParameter("restaurantId"));

MenuDaoImp mdo = new MenuDaoImp();
RestaurantDaoImp rdo = new RestaurantDaoImp();

List<Menu> menuList = mdo.getMenuByResId(restaurantId);
Restaurant restaurant = rdo.getRestaurant(restaurantId);

request.setAttribute("allMenu", menuList);
request.setAttribute("restaurant", restaurant);

request.getRequestDispatcher("Menu.jsp")
  .forward(request, response);
}
}
