/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import database.tables.EditBookingsTable;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author user
 */
@WebServlet(name = "respondToBooking", urlPatterns = {"/respondToBooking"})
public class respondToBooking extends HttpServlet {

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
        EditBookingsTable table = new EditBookingsTable();
        String user = request.getParameter("user");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(user);
        JsonObject jo = new JsonObject();
        String res = jsonNode.get("response").asText();
        String pet_id = jsonNode.get("pet_id").asText();
        String keeper_id = jsonNode.get("id").asText();

        try {
            table.SQLquery("UPDATE bookings SET status ='" + res + "' WHERE keeper_id = '" + keeper_id + "' AND pet_id = '" + pet_id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }

        jo.addProperty("size", user);
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

}
