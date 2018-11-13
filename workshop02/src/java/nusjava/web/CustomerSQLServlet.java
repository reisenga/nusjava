
package nusjava.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;


@WebServlet(urlPatterns = {"/customer-sql"})
public class CustomerSQLServlet extends HttpServlet{

    
    @Resource(lookup = "jdbc/sample")
    private DataSource sampleDS;
        
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        //read the custid from field
        Integer custID = Integer.parseInt(req.getParameter("custID"));
        
        //get a connection
        try (Connection conn = sampleDS.getConnection()) {
        
      
        //create a prepared statement
        PreparedStatement ps = conn.prepareStatement("select * from customer where customer_id = ?");
        
        //set the parameter
        ps.setInt(1, custID);

        //Execute the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType(MediaType.TEXT_PLAIN);
                try (PrintWriter pw = resp.getWriter()) {
                pw.printf("id: %d, name: %s, address: %s, phone: %s, email: %s\n,",
                        rs.getInt("customer_id"), rs.getString("name"),
                        rs.getString("addressline1"),rs.getString("phone"),
                        rs.getString("email"));
                            
                }
                
            }
            else {
                //customer id is not found
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType(MediaType.TEXT_PLAIN);
                try (PrintWriter pw = resp.getWriter()) {
                    pw.printf("Customer with %d does not exists", custID);
                }
            }
          
            
            
        } catch (SQLException exception) {
            log(exception.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try (PrintWriter pw = resp.getWriter()) {
                pw.println(exception.getMessage());
            }
        }
        
        
    }


}
