package workshop01.web;

import java.awt.PageAttributes;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

@WebServlet(urlPatterns = { "/weather" } )
public class WeatherServlet extends HttpServlet {
    
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String APPID = "2c2d770f0baa75bc277e8b8588536812";
    private Client client;
    
    
    //Initialize the client
    @Override
    public void init() throws ServletException {
        client = ClientBuilder.newClient();
    }

    //Cleanup
    @Override
    public void destroy() {
        client.close();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Form Field Name
        String cityname = req.getParameter("cityname");
        
        //Added validation to check for spaces and default city to SG if no entry
        if ((cityname == null) || (cityname.trim().length() <= 0)) {
            cityname = "Singapore";
        }

        //Create the target
        WebTarget target = client.target(WEATHER_URL);
        
        //set the query string
        target = target.queryParam("q", cityname);
        target = target.queryParam("appid", APPID);                
        target = target.queryParam("units", "metric");
        
        //create invocation; expect JSON as result
        Invocation.Builder inv = target.request(MediaType.APPLICATION_JSON);
        
        //Make the call using GET HTTP method;
        //result will be null if there are error
        JsonObject result = inv.get(JsonObject.class);
        JsonArray weatherDetails = result.getJsonArray("weather");
        
        
        //Echo back the name
        //Set the 200 OK status code
        resp.setStatus(HttpServletResponse.SC_OK);
        
        //set the content type/media type
        resp.setContentType(MediaType.TEXT_HTML);
        
        try (PrintWriter pw = resp.getWriter()) {
            pw.print("<h2>The weather for " + cityname.toUpperCase() + "</h2>");
            for (int i = 0; i < weatherDetails.size(); i++) {
                JsonObject wd = weatherDetails.getJsonObject(i);
                String main = wd.getString("main");
                String description = wd.getString("description");
                String icon = wd.getString("icon");
                pw.print("<div>");
                pw.print(main + " &dash; " + description);
                pw.printf("<img src=\"http://openweathermap.org/img/w/%s.png\">" , icon);
                pw.print("</div");
            }
        }
    }
}