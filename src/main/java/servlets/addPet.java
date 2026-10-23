/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import database.tables.EditPetsTable;

/**
 *
 * @author user
 */
public class addPet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        EditPetsTable pets = new EditPetsTable();
        String res = "";
        try {
            res = pets.addPetFromJSON(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("res", res);
        jo.addProperty("str", "pet was added successfully");

        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        JsonObject jo = new JsonObject();

        jo.addProperty("str", user);
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }

}
