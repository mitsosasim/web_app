/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import database.tables.EditPetKeepersTable;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.PetKeeper;

/**
 *
 * @author user
 */
@WebServlet(name = "getUsers", urlPatterns = {"/getUsers"})
public class getUsers extends HttpServlet {
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EditPetKeepersTable table = new EditPetKeepersTable();
        ArrayList<PetKeeper> list = new ArrayList<PetKeeper>();
        String user = request.getParameter("text");
        JsonObject jo = new JsonObject();


        try {
            list = table.getAllKeepers();
            for (int i = 0; i < list.size(); i++) {
                jo.addProperty("keeper" + i, table.databasePetKeeperToJSON(list.get(i).getUsername(), list.get(i).getPassword()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jo.addProperty("size", list.size());
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }
}
