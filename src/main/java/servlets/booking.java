/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import database.tables.EditBookingsTable;

/**
 *
 * @author user
 */
@WebServlet(name = "booking", urlPatterns = {"/booking"})
public class booking extends HttpServlet {


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = request.getParameter("user");
        ObjectMapper objectMapper = new ObjectMapper();
        EditBookingsTable table = new EditBookingsTable();
        JsonObject jo = new JsonObject();

//        System.out.println(user);
        try {
            table.addBookingFromJSON(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        jo.addProperty("str", "booking was successfully made");
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
}
