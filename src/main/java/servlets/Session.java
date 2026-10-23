package servlets;

import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import database.tables.EditPetKeepersTable;
import database.tables.EditPetOwnersTable;
import database.tables.EditMessagesTable;
import java.util.ArrayList;
import mainClasses.PetKeeper;
import mainClasses.PetOwner;


public class Session extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject jo = new JsonObject();

        HttpSession session = request.getSession(true);
        String user = (String) session.getValue("user");
        EditPetKeepersTable keepers = new EditPetKeepersTable();
        EditPetOwnersTable owners = new EditPetOwnersTable();
        EditMessagesTable msgs = new EditMessagesTable();

        if (user == null) {
            jo.addProperty("str", "user does not exist");
            jo.addProperty("color", "red");
            jo.addProperty("buttonDisplay", "none");
            jo.addProperty("display", "block");
        } else if (user == "admin") {
            jo.addProperty("str", "admin");
            jo.addProperty("color", "green");
            jo.addProperty("buttonDisplay", "block");
            jo.addProperty("display", "none");
            ArrayList<PetKeeper> allKeepers = new ArrayList<PetKeeper>();
            ArrayList<PetOwner> allOwners = new ArrayList<PetOwner>();
            try {
                allKeepers.addAll(keepers.getKeepers("dogkeeper"));
                allKeepers.addAll(keepers.getKeepers("catkeeper"));
                allOwners.addAll(owners.getOwners());
                jo.addProperty("osize", allOwners.size());
                jo.addProperty("ksize", allKeepers.size());
                for (int i = 0; i < allOwners.size(); i++) {
                    jo.addProperty("owner" + i, owners.databasePetOwnerToJSON(allOwners.get(i).getUsername(), allOwners.get(i).getPassword()));
                }
                for (int i = 0; i < allKeepers.size(); i++) {
                    jo.addProperty("keeper" + i, keepers.databasePetKeeperToJSON(allKeepers.get(i).getUsername(), allKeepers.get(i).getPassword()));
                }
            } catch (Exception e) {
                    e.printStackTrace();
            }
            try {
                jo.addProperty("data0", msgs.SQLquery("SELECT COUNT(*) FROM pets WHERE type = 'cat';").substring(8));
                jo.addProperty("data1", msgs.SQLquery("SELECT COUNT(*) FROM pets WHERE type = 'dog';").substring(8));
                jo.addProperty("data2", msgs.SQLquery("SELECT COUNT(*) FROM petkeepers WHERE 1;").substring(8));
                jo.addProperty("data3", msgs.SQLquery("SELECT COUNT(*) FROM petowners WHERE 1;").substring(8));
                String res = msgs.SQLquery("SELECT SUM(price) AS total_price FROM bookings WHERE status = 'finished';");
                double revenue = Double.parseDouble(res.substring(13, res.length() - 2));
                String finalStr = String.valueOf(0.85 * revenue);
                jo.addProperty("data4", finalStr);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            jo.addProperty("str", user);
            jo.addProperty("color", "green");
            jo.addProperty("buttonDisplay", "block");
            jo.addProperty("display", "none");
        }

        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }


}
