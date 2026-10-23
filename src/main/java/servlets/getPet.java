/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import database.tables.EditPetsTable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author user
 */
@WebServlet(name = "getPet", urlPatterns = {"/getPet"})
public class getPet extends HttpServlet {

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
        EditPetsTable table = new EditPetsTable();
        JsonObject jo = new JsonObject();

        String owner_id = "";
        JsonNode jsonNode = objectMapper.readTree(user);

        owner_id = jsonNode.get("owner_id").asText();
        try {
            jo.addProperty("str", table.petOfOwner(owner_id).getPet_id());
        } catch (Exception e) {

        }

        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }
}
