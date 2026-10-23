package demo;

import database.DB_Connection;
import database.init.InitDatabase;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

/** Runs the real servlet application against a disposable, synthetic database. */
public class DemoServer {
    public static Tomcat start(int port) throws Exception {
        Class.forName("org.h2.Driver");
        System.setProperty("DB_URL", "jdbc:h2:mem:petcare" + port + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        System.setProperty("DB_USER", "sa");
        System.setProperty("DB_PASSWORD", "");
        new InitDatabase().initTables();
        try (Connection connection = DB_Connection.getConnection(); Statement statement = connection.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("sql/demo-data.sql")), StandardCharsets.UTF_8).replaceAll("(?m)--.*$", "");
            for (String part : sql.split(";")) if (!part.trim().isEmpty()) statement.execute(part);
        }
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("target/tomcat-" + port);
        tomcat.setHostname("127.0.0.1");
        tomcat.setPort(port);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        Context context = tomcat.addWebapp("/pet-care", new File("src/main/webapp").getAbsolutePath());
        // This application serves HTML and servlets, with no JSP runtime.
        context.addLifecycleListener(event -> {
            if (org.apache.catalina.Lifecycle.CONFIGURE_START_EVENT.equals(event.getType())) {
                org.apache.catalina.Container jsp = context.findChild("jsp");
                if (jsp != null) context.removeChild(jsp);
                context.removeServletMapping("*.jsp");
                context.removeServletMapping("*.jspx");
            }
        });
        context.setParentClassLoader(DemoServer.class.getClassLoader());
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
        context.setResources(resources);
        tomcat.start();
        return tomcat;
    }

    public static void main(String[] args) throws Exception {
        if (System.getenv("OPENAI_API_KEY") != null) {
            throw new IllegalStateException("Run the local demo without OPENAI_API_KEY to keep external API calls disabled.");
        }
        int port = Integer.parseInt(System.getProperty("demo.port", "8080"));
        Tomcat server = start(port);
        System.out.println("Pet Care demo: http://127.0.0.1:" + server.getConnector().getLocalPort() + "/pet-care/");
        server.getServer().await();
    }
}
