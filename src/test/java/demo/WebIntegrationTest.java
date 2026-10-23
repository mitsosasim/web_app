package demo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.DB_Connection;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class WebIntegrationTest {
    static Tomcat server;
    static String base;
    int status;

    @BeforeAll static void start() throws Exception {
        server = DemoServer.start(0);
        base = "http://127.0.0.1:" + server.getConnector().getLocalPort() + "/pet-care/";
    }
    @AfterAll static void stop() throws Exception { if (server != null) { server.stop(); server.destroy(); } }
    @BeforeEach void newSession() { CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL)); }

    String request(String method, String path, String json) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(base + path).openConnection();
        connection.setRequestMethod(method);
        connection.setInstanceFollowRedirects(false);
        if (json != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] body = ("user=" + URLEncoder.encode(json, "UTF-8")).getBytes(StandardCharsets.UTF_8);
            try (OutputStream out = connection.getOutputStream()) { out.write(body); }
        }
        status = connection.getResponseCode();
        InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (stream == null) return "";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (InputStream input = stream) { byte[] chunk = new byte[4096]; int n; while ((n = input.read(chunk)) >= 0) bytes.write(chunk,0,n); }
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }
    JsonObject json(String text) { return JsonParser.parseString(text).getAsJsonObject(); }

    @Test void visitorCanLoadPageAndKeeperDirectory() throws Exception {
        assertTrue(request("GET", "", null).contains("Pet Care"));
        assertEquals(200, status);
        JsonObject directory = json(request("GET", "getUsers", null));
        assertEquals(3, directory.get("size").getAsInt());
        assertTrue(directory.toString().contains("demo_keeper"));
    }
    @Test void ownerLoginPersistsUntilLogout() throws Exception {
        request("POST", "LoginSession", "{\"username\":\"demo_owner\",\"password\":\"Demo123!\"}");
        assertEquals(302, status);
        JsonObject session = json(request("GET", "Session", null));
        assertEquals(1, json(session.get("str").getAsString()).get("owner_id").getAsInt());
        request("POST", "LogoutSession", "{}");
        assertEquals("user does not exist", json(request("GET", "Session", null)).get("str").getAsString());
    }
    @Test void invalidLoginDoesNotCreateAuthenticatedSession() throws Exception {
        request("POST", "LoginSession", "{\"username\":\"demo_owner\",\"password\":\"wrong\"}");
        assertEquals("user does not exist", json(request("GET", "Session", null)).get("str").getAsString());
    }
    @Test void keeperSeesRequestedBooking() throws Exception {
        JsonObject result = json(request("POST", "getBookingToKeeper", "{\"id\":\"1\"}"));
        JsonObject booking = json(result.get("str").getAsString());
        assertEquals(1, booking.get("booking_id").getAsInt());
        assertEquals("2026-11-01", booking.get("fromdate").getAsString());
        assertEquals("2026-11-03", booking.get("todate").getAsString());
        assertEquals("requested", booking.get("status").getAsString());
        assertEquals("1000000001", booking.get("pet_id").getAsString());
    }
    @Test void ownerGetsUnoccupiedKeepers() throws Exception {
        JsonObject result = json(request("POST", "getAvailableKeepers", "{\"name\":\"demo_owner\",\"password\":\"Demo123!\"}"));
        assertEquals(2, result.get("fasize").getAsInt());
    }
    @Test void adminGetsUserCountsAndFinishedBookingShare() throws Exception {
        request("POST", "LoginSession", "{\"username\":\"admin\",\"password\":\"admin12*\"}");
        JsonObject session = json(request("GET", "Session", null));
        assertEquals("admin", session.get("str").getAsString());
        assertEquals(1, session.get("osize").getAsInt());
        assertEquals(3, session.get("ksize").getAsInt());
        assertEquals(25.5, session.get("data4").getAsDouble(), 0.001);
    }
    @Test void foreignKeysRejectMissingPetOwner() throws Exception {
        try (Connection db = DB_Connection.getConnection(); Statement statement = db.createStatement()) {
            assertThrows(SQLException.class, () -> statement.executeUpdate("INSERT INTO pets VALUES ('9999999999',999,'Demo','dog','Mixed','other',2020,5,'','')"));
        }
    }
    @Test void externalAssistantIsOffAndMaintenanceRoutesAreAbsent() throws Exception {
        request("POST", "ChatGPT", "demo"); assertEquals(503, status);
        request("GET", "InitDB", null); assertEquals(404, status);
        request("GET", "DeleteDB", null); assertEquals(404, status);
    }
}
