/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import de.beinlich.markus.pizzaservice.model.OrderHeader;
import java.io.Serializable;

/**
 *
 * @author Markus Beinlich
 */
public class OrderEvent implements Serializable{
    
    private static final long serialVersionUID = 3087119434882768952L;
    private OrderHeader order;

    public OrderEvent() {
    }
    @Override
    public String toString() {
        return "OrderEvent:";
    }

    public OrderHeader getOrder() {
        return order;
    }

    public void setOrder(OrderHeader order) {
        this.order = order;
    }
    
}
