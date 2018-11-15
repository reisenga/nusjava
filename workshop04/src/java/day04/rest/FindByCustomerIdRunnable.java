
package day04.rest;

import day04.business.CustomerBean;
import day04.model.Customer;
import java.sql.SQLException;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;


public class FindByCustomerIdRunnable implements Runnable{

    private Integer custId;
    private CustomerBean customerBean;
    private AsyncResponse asyncResp;
    
    public FindByCustomerIdRunnable(Integer cid, 
            CustomerBean cb, AsyncResponse ar) {
        custId = cid;
        customerBean = cb;
        asyncResp = ar;
      
    }
    
    @Override
    public void run() {
    
        System.out.println("running FindByCustomerIdRunnable");
        
        Optional<Customer> opt = null;
        try {
            opt = customerBean.findByCustomerId(custId);
            
        } catch (SQLException ex) {
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            
            //this one returns a 500 Server error
            asyncResp.resume(Response.serverError().entity(error).build());
            return;
        }
        //to return 404 is the customer is not found
        if (!opt.isPresent()) {
            asyncResp.resume(Response.status(Response.Status.NOT_FOUND).build());
            return;
            
        }
        //return the data as JSON object
        asyncResp.resume(Response.ok(opt.get().toJson()).build());
        
    
    }
    
       
}
