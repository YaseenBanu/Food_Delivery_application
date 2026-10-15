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
import jakarta.servlet.http.HttpSession;
@WebServlet("/callloginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        HttpSession session = req.getSession();

        UserDaoImp dao = new UserDaoImp();
        User user = dao.getUserByMail(email);
        if(user != null &&
           BCrypt.checkpw(password, user.getPassword()))
        {
            session.setAttribute("loggedInUser", user);

            // NEW — route each role to its own home screen instead of
            // always sending everyone to the customer restaurant listing.
            String role = user.getRole();
            if ("Admin".equals(role)) {
                resp.sendRedirect("adminDashboardServlet");
            } else if ("Delivery agent".equals(role)) {
                resp.sendRedirect("deliveryDashboardServlet");
            } else {
                resp.sendRedirect("callRestaurantServlet");
            }
        }
        else{
            resp.sendRedirect("invalid.html");
        }

    }
}