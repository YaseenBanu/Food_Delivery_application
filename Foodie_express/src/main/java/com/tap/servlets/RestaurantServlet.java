package com.tap.servlets;

import java.io.IOException;
import java.util.List;

import com.tap.Dao.RestaurantDao;
import com.tap.DaoImp.RestaurantDaoImp;
import com.tap.model.Restaurant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/callRestaurantServlet")
public class RestaurantServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RestaurantDao restaurantDao;

    @Override
    public void init() throws ServletException {
        restaurantDao = new RestaurantDaoImp();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("login.html");
            return;
        }
        try {
            List<Restaurant> lst = restaurantDao.getAllRestaurant();
            request.setAttribute("allRestaurants", lst);
            request.getRequestDispatcher("Restaurant.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load restaurants.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}