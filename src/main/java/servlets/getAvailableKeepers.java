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
import database.tables.EditBookingsTable;
import database.tables.EditPetKeepersTable;
import database.tables.EditPetOwnersTable;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import mainClasses.PetKeeper;

/**
 *
 * @author user
 */
@WebServlet(name = "getAvailableKeepers", urlPatterns = {"/getAvailableKeepers"})
public class getAvailableKeepers extends HttpServlet {

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
        JsonNode jsonNode2 = null;
        JsonNode jsonNode3 = null;
        String oid = "";
        String name = jsonNode.get("name").asText();
        String password = jsonNode.get("password").asText();

        EditPetKeepersTable ptable = new EditPetKeepersTable();
        EditPetOwnersTable otable = new EditPetOwnersTable();
        int size = 0;
        int size2 = 0;
        try {
            System.out.println("this is the user: " + otable.databasePetOwnerToJSON(name, password));
            jsonNode2 = objectMapper.readTree(otable.databasePetOwnerToJSON(name, password));
            oid = jsonNode2.get("owner_id").asText();
        } catch (Exception e) {

        }
        ArrayList<PetKeeper> list = new ArrayList<PetKeeper>();
        ArrayList<PetKeeper> list2 = new ArrayList<PetKeeper>();
        ArrayList<PetKeeper> list3 = new ArrayList<PetKeeper>();
        JsonObject jo = new JsonObject();
        try {
            list = table.getAvailableKeepersForOwner("SELECT *\n"
                    + "FROM petkeepers\n"
                    + "WHERE EXISTS (\n"
                    + "    SELECT 1\n"
                    + "    FROM bookings\n"
                    + "    WHERE bookings.keeper_id = petkeepers.keeper_id\n"
                    + "      AND bookings.status IN ('accepted', 'requested')\n"
                    + ");");

            list2 = table.getAvailableKeepersForOwner("SELECT * FROM `petkeepers` WHERE EXISTS ( SELECT 1 FROM `bookings` \n"
                    + "WHERE bookings.keeper_id = petkeepers.keeper_id AND bookings.status = 'finished') ");

            list3 = table.getAvailableKeepersForOwner("SELECT *\n"
                    + "FROM petkeepers\n"
                    + "WHERE NOT EXISTS (\n"
                    + "    SELECT 1\n"
                    + "    FROM bookings\n"
                    + "    WHERE bookings.keeper_id = petkeepers.keeper_id\n"
                    + "      AND bookings.status IN ('accepted', 'requested')\n"
                    + ");");

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                jsonNode3 = objectMapper.readTree(ptable.databasePetKeeperToJSON(list.get(i).getUsername(), list.get(i).getPassword()));
                if (table.bookingExists(Integer.parseInt(oid), Integer.parseInt(jsonNode3.get("keeper_id").asText()))) {
                    jo.addProperty("keeper" + size, ptable.databasePetKeeperToJSON(list.get(i).getUsername(), list.get(i).getPassword()));
                    size++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < list2.size(); i++) {
            try {
                jsonNode3 = objectMapper.readTree(ptable.databasePetKeeperToJSON(list2.get(i).getUsername(), list2.get(i).getPassword()));
                if (table.bookingExists(Integer.parseInt(oid), Integer.parseInt(jsonNode3.get("keeper_id").asText()))) {
                    jo.addProperty("fkeeper" + i, ptable.databasePetKeeperToJSON(list2.get(i).getUsername(), list2.get(i).getPassword()));
                    size2++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < list3.size(); i++) {
            try {
                jsonNode3 = objectMapper.readTree(ptable.databasePetKeeperToJSON(list3.get(i).getUsername(), list3.get(i).getPassword()));
                jo.addProperty("fakeeper" + i, ptable.databasePetKeeperToJSON(list3.get(i).getUsername(), list3.get(i).getPassword()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jo.addProperty("size", size);
        jo.addProperty("fsize", size2);
        jo.addProperty("fasize", list3.size());
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }


}
