/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.jms;

import de.beinlich.markus.pizzaservice.ejb.OrderEjbRemote;
import de.beinlich.markus.pizzaservice.model.OrderHeader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Markus Beinlich
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/PizzaOrderQueue")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class OrderMdb implements MessageListener {

    @EJB
    private OrderEjbRemote orderEjb;
    
    public OrderMdb() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            orderEjb.saveOrder(message.getBody(OrderHeader.class));
        } catch (JMSException ex) {
            Logger.getLogger(OrderMdb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
