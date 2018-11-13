
package nushava.web;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Webservlet(urlPatterns = { "/purchaseOrder" })


public class PurchaseOrderServlet extends HttpServlet {

    @PersistenceContext private EntityManager em;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        Integer custID = Integer.parseInt(req.getParameter("custID"));
        
        TypedQuery<PurchaseOrder> query em.createNamedQuery("PurchaseOrder.findByCustomerId", PurchaseOrder.class);
        
        query.setParameter("custID", custID);
        
        List<PurchaseOrder> result = query.getResultList();
        
    
    }


}
