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
import javax.servlet.http.HttpSession;
import database.tables.EditPetOwnersTable;
import database.tables.EditPetKeepersTable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        HttpSession session = request.getSession(true);
        String username = "";
        String password = "";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(user);
        username = jsonNode.get("username").asText();
        password = jsonNode.get("password").asText();
        EditPetOwnersTable o = new EditPetOwnersTable();
        EditPetKeepersTable k = new EditPetKeepersTable();

        if (username.equals("admin") && password.equals("admin12*")) {
            session.putValue("user", "admin");
        } else {
            try {
                if (o.databasePetOwnerToJSON(username, password) == null) {
                    if (k.databasePetKeeperToJSON(username, password) != null) {
                        session.putValue("user", k.databasePetKeeperToJSON(username, password));
                    } else {
                        session.putValue("user", null);
                    }
                } else {
                    session.putValue("user", o.databasePetOwnerToJSON(username, password));
                }
            } catch (Exception e) {
                session.putValue("user", null);
            }
        }
        response.sendRedirect(request.getContextPath() + "/Session");
    }

    private boolean userExists(String username, String password) throws Exception {
        EditPetOwnersTable o = new EditPetOwnersTable();
        EditPetKeepersTable k = new EditPetKeepersTable();
        try {
            if (o.databasePetOwnerToJSON(username, password) != null) {
                return true;
            } else if (k.databasePetKeeperToJSON(username, password) != null) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
