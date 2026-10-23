/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import database.tables.EditPetOwnersTable;

public class AddOwner extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        JsonObject jo = new JsonObject();
        String username = "";
        String email = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(user);

            username = jsonNode.get("username").asText();
            email = jsonNode.get("email").asText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!OwnerExists(username, "username") && !OwnerExists(email, "email")) {
                response.setStatus(200);
                addOwnerUser(user);
                jo.addProperty("str", "Pet owner was added successfully");
                jo.addProperty("color", "green");
            } else {
                response.setStatus(403);
                jo.addProperty("str", "Pet owner already exists");
                jo.addProperty("color", "red");
            }
        } catch (ClassNotFoundException e) {
            jo.addProperty("str", "Error loading class");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (SQLException e) {
            jo.addProperty("str", "SQL exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

    private boolean OwnerExists(String element, String str) throws ClassNotFoundException, SQLException {
        EditPetOwnersTable table = new EditPetOwnersTable();

        return table.petOwnerExists(element, str);
    }

    private void addOwnerUser(String user) throws ClassNotFoundException, SQLException {
        EditPetOwnersTable table = new EditPetOwnersTable();
        table.addPetOwnerFromJSON(user);
    }

}
