package com.tap.servlets;
import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.tap.DaoImp.UserDaoImp;
import com.tap.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userName = req.getParameter("userName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String address = req.getParameter("address");
        String role = req.getParameter("role");

        String hpwd=BCrypt.hashpw(password, BCrypt.gensalt(8));
        
        User user = new User(userName,hpwd,email,address,role);
        
        UserDaoImp dao = new UserDaoImp();

        try {

            int result = dao.addUser(user);

            if (result > 0) {
                resp.sendRedirect("login.html");
            } else {
                resp.getWriter().println("Registration Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("Error : " + e.getMessage());
        }
        }
}
	

