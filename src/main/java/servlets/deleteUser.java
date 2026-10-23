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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import database.tables.EditPetKeepersTable;
import database.tables.EditPetOwnersTable;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author user
 */
@WebServlet(name = "deleteUser", urlPatterns = {"/deleteUser"})
public class deleteUser extends HttpServlet {
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
        JsonObject jo = new JsonObject();
        EditPetKeepersTable table1 = new EditPetKeepersTable();
        EditPetOwnersTable table2 = new EditPetOwnersTable();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(user);

        String username = "";
        String email = "";
        String password = "";
        System.out.println(user);

        try {

            username = jsonNode.get("name").asText();
            email = jsonNode.get("email").asText();
            password = jsonNode.get("password").asText();

            table1.deletePetKeeper(username, email, password);
            table2.deletePetOwner(username, email, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        jo.addProperty("str", "user was deleted successfully");
        jo.addProperty("color", "green");
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }


}
