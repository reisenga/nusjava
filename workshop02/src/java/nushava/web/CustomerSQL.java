/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nushava.web;


import javax.annotation.Resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;


@WebServlet(urlPatterns = { "/customer-sql"})
public class CustomerSQL extends HttpServlet{
    
    //Get a reference to the SamplePool jdbc/sample
    @Resource(lookup = "jdbc/sample")
    private DataSource sampleDS;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //Read the custid form field      
        Integer custID = Integer.parseInt(req.getParameter("custID"));
            
      //Get a connection
      try (Connection conn = sampleDS.getConnection()) {
          
      //Create a PreparedStatement
      
      PreparedStatement ps = conn.prepareStatement("select * from customer where customer_id = ? ");
      
      
      //Set the parameter
      ps.setInt(1, custID);
      //Execute the query
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
          //we found a record
        
          resp.setstatus(httpservletresponse.SC_OK);
          resp.setcontenttype(Mediatype.text_plain);
          try (PrintWriter pw = resp.getwriter()) {
              pw.printf("id: %d, name: %s, address: %s, phone: %s, email: %s\n,",
                      rs.getInt("customer_id"), rs.getstring("name"),
                      rs.getString("addressline1"), rs.getstring("phone"),
                      rs.getstring("email));
                      
          }
      } else    
          //Customer id is not found
          resp.setstatus(httpservletresponse.SC_NOT_FOUND);
          resp.setcontenttype(Mediatype.text_plain);
          try (PrintWriter pw = resp.getwriter()) {
              pw.printf("customer with ID %d does not exists", custID);
              
          )
          }
          
      }
        
      } catch (SQLException ex) {
          log(ex.getMessage());
          resp.setStatus(HttpServletResposne.SC_INTERNAL_SERVER_ERROR);
          resp.setContentType(MediaType.TEXT_PLAIN);
          try (PrintWriter pw = resp.getWriter()) {
              pw.println(ex.getMessage());
          }
          
          
          
      }
            }
    }
            
    
    
