/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import de.beinlich.markus.pizzaservice.model.Customer;
import de.beinlich.markus.pizzaservice.model.OrderHeader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Markus Beinlich
 */
@Stateless(mappedName = "ejb/orderEjb")
public class OrderEjb implements OrderEjbRemote {

//    @PersistenceUnit(unitName = "pizzajpa")
//    private EntityManagerFactory emf;
    @PersistenceContext(unitName = "pizzajpa")
    private EntityManager em;

    @Inject
    private Event<OrderEvent> orderEvent;

    @Override
    public void saveOrder(OrderHeader order) {
        try {
//            EntityManager em = emf.createEntityManager();
//            em.persist(order.getCustomer());
//            em.persist(order.getCustomer());
            order = em.merge(order);
//            Customer customer;
//            customer = order.getCustomer();
//            em.persist(customer);
            em.flush();
            System.out.println("saveOrder fire1------------------------>");
            OrderEvent newOrder = new OrderEvent();
            newOrder.setOrder(order);
            orderEvent.fire(newOrder);
            System.out.println("saveOrder fire2");
        } catch (SecurityException | IllegalStateException ex) {
            Logger.getLogger(OrderEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
