/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

/**
 *
 * @author Markus Beinlich
 */
@Stateless
//@MessageDriven(mappedName = "jms/PizzaTopic",
//        activationConfig = {
//            @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")})
public class OrderMessageEjb {

    @Resource(lookup = "jms/PizzaTopic")
    private Topic destination;

    @Inject
    @JMSConnectionFactory("jms/PizzaTopicConnectionFactory")
    private JMSContext context;

    public void getNewOrderHeader(@Observes OrderEvent orderEvent) {

        System.out.println("getNewOrderHeader1:" + orderEvent.getOrder().getOrderDate());
        try {
            JMSProducer producer = context.createProducer();
            ObjectMessage objectMessage = context.createObjectMessage();
            objectMessage.setObject(orderEvent.getOrder());
            producer = producer.send(destination, objectMessage);
            System.out.println("producer:" + producer.getJMSCorrelationID());
        } catch (JMSException ex) {
            Logger.getLogger(OrderMessageEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
