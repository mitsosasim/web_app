/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import com.google.gson.JsonObject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author user
 */
public class LogoutSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject jo = new JsonObject();
        HttpSession session = request.getSession();

        if (session.getAttribute("user") != null) {
            jo.addProperty("str", "user logged out successfully");
            session.invalidate();
            response.setStatus(200);
        } else {
            response.setStatus(403);
        }

        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

}
