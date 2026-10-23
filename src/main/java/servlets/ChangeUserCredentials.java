/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.JsonObject;
import javax.servlet.http.HttpServletResponse;
import database.tables.EditPetKeepersTable;
import database.tables.EditPetOwnersTable;

/**
 *
 * @author user
 */
public class ChangeUserCredentials extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        PrintWriter out = response.getWriter();
        JsonObject jo = new JsonObject();
        String user = request.getParameter("user");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(user);
        String username = jsonNode.get("username").asText();
        String password = jsonNode.get("password").asText();
        EditPetKeepersTable table = new EditPetKeepersTable();
        EditPetOwnersTable table2 = new EditPetOwnersTable();
        try {
            if (table.jsonToPetKeeper(user) != null) {
                table.updatePetKeeperCredentials(username, password, table.jsonToPetKeeper(user));

            }
            if (table2.jsonToPetOwner(user) != null) {
                table2.updatePetOwnerCredentials(username, password, table2.jsonToPetOwner(user));
            }

        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
        }


        jo.addProperty("str", user);

        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }


}
