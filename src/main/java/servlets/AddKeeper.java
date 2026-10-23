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
import com.google.gson.JsonObject;
import database.tables.EditPetKeepersTable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddKeeper extends HttpServlet {

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
            if (!KeeperExists(username, "username") && !KeeperExists(email, "email")) {
                addKeeperUser(user);
                response.setStatus(200);
                jo.addProperty("str", "Pet keeper was added successfully");
                jo.addProperty("color", "green");
            } else {
                response.setStatus(403);
                jo.addProperty("str", "Pet keeper already exists");
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

    private boolean KeeperExists(String element, String str) throws ClassNotFoundException, SQLException {
        EditPetKeepersTable table = new EditPetKeepersTable();

        return table.PetKeeperExists(element, str);
    }

    private void addKeeperUser(String user) throws ClassNotFoundException, SQLException {
        EditPetKeepersTable table = new EditPetKeepersTable();
        table.addPetKeeperFromJSON(user);
    }

}
