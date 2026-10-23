package servlets;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ChatGPT", urlPatterns = {"/ChatGPT"})
public class ChatGPT extends HttpServlet {

    private boolean API_KEY_MISSING() {
        return System.getenv("OPENAI_API_KEY") == null || System.getenv("OPENAI_API_KEY").trim().isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (API_KEY_MISSING()) {
            response.setStatus(503);
            response.setContentType("application/json");
            response.getWriter().write("{\"str\":\"Pet-care assistant is not configured.\"}");
            return;
        }
        String text = request.getParameter("user");
        JsonObject jo = new JsonObject();
        jo.addProperty("str", ChatGPTAPIExample.chatGPT(text));
        response.setContentType("application/json");
        response.getWriter().write(jo.toString());
    }
}

class ChatGPTAPIExample {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    public static String chatGPT(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            JsonObject bodyJson = new JsonObject();
            bodyJson.addProperty("model", MODEL);
            com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            messages.add(message);
            bodyJson.add("messages", messages);

            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = bodyJson.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return extractMessageFromJSONResponse(response.toString());
                }
            } else {
                return "Error: HTTP response code: " + responseCode;
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String extractMessageFromJSONResponse(String response) {
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        String message = jsonObject.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
        return message;
    }
}
