/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import database.tables.EditPetsTable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;

/**
 *
 * @author user
 */
public class PetCredentials extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        JsonObject jo = new JsonObject();
        EditPetsTable pet = new EditPetsTable();
        String id = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(user);
            id = jsonNode.get("owner_id").asText();
            JsonNode jsonNode2 = objectMapper.readTree(pet.petToJSON(pet.petOfOwner(id)));
            String exists = jsonNode2.get("pet_id").asText();
            jo.addProperty("str", exists != "0");
        } catch (Exception e) {
            e.printStackTrace();
        }


        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

}
