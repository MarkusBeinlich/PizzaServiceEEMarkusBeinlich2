/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import de.beinlich.markus.pizzaservice.model.OrderHeader;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Markus Beinlich
 */
//@MessageDriven(mappedName = "jms/PizzaTopic",
//        activationConfig = {
//            @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")})
@Stateless(mappedName = "ejb/orderTableEjb")
public class OrderTableEjb implements OrderTableEjbRemote {
///*    @PersistenceUnit(unitName = "pizzajpa")*/

//    private EntityManagerFactory emf;
    @PersistenceContext(unitName = "pizzajpa")
    private EntityManager em;

    @Override
    public List<OrderHeader> getAllOrderHeader() {

//        emf = Persistence.createEntityManagerFactory("pizzajpa");
//        EntityManager em = emf.createEntityManager();
        TypedQuery<OrderHeader> query = em.createNamedQuery(OrderHeader.findAll, OrderHeader.class);
        List<OrderHeader> orders = query.getResultList();

        System.out.println("getAllOrderHeader1:");

        return orders;
    }
}
