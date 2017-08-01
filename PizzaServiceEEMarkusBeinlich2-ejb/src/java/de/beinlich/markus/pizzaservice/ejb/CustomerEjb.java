/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import de.beinlich.markus.pizzaservice.model.Customer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Markus Beinlich
 */
@Stateless(mappedName = "ejb/customerEjb")
public class CustomerEjb implements CustomerEjbRemote {

    @PersistenceContext(unitName = "pizzajpa")
    private EntityManager em;

    @Override
    public Customer getCustomerByEmail(String email) {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.findByEmail, Customer.class);
        query.setParameter("email", email);
        List<Customer> customers = query.getResultList();
        if (customers.isEmpty()) {
            return null;
        } else {
            return customers.get(0);
        }
    }

    @Override
    public void addCustomer(Customer customer) {
        em.persist(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return em.merge(customer);
    }

}
