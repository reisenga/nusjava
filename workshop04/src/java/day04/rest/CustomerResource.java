
package day04.rest;

import day04.business.CustomerBean;
import day04.model.Customer;
import java.sql.SQLException;
import java.util.Optional;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/customer")
public class CustomerResource {
    
    @EJB private CustomerBean customerBean;
    
    @Resource(lookup = "concurrent/mythreadpool")
    private ManagedScheduledExecutorService threadPool;
    
    // Get Customer /1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{custId}")
    public Response findByCustomerId(@PathParam("custId") Integer custId) {
     
        Optional<Customer> opt = null;
        try {
            opt = customerBean.findByCustomerId(custId);
            
        } catch (SQLException ex) {
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            
            //this one returns a 500 Server error
            return Response.serverError().entity(error).build();
            
        }
        
        //to return 404 is the customer is not found
        if (!opt.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //return the data as JSON object
        return Response.ok(opt.get().toJson()).build();
    }
    
    //Get customer/async/1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("async/{custId}")
    public void findByAsyncCustomerId(
            @PathParam("custId") Integer custId,
            @Suspended AsyncResponse asyncResp) {
     
        FindByCustomerIdRunnable runnable = 
                new FindByCustomerIdRunnable(custId, customerBean, asyncResp);
                
        //execute the runable threadpool
        threadPool.execute(runnable);
        
        System.out.println("exiting findByAsyncCustomerId");
    }
    
}

