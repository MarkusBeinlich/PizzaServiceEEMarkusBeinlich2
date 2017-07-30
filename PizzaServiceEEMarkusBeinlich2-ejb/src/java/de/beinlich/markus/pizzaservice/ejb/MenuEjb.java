/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.ejb;

import de.beinlich.markus.pizzaservice.model.Menu;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Markus Beinlich
 */
@Stateless(mappedName = "ejb/menuEjb")
public class MenuEjb implements MenuEjbRemote {

//    @PersistenceUnit(unitName = "pizzajpa")
//    private EntityManagerFactory emf;
    @PersistenceContext(unitName = "pizzajpa")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Menu getMenu(Menu menu) {
        if (menu.getMenuItems().isEmpty()) {
//            EntityManager em = emf.createEntityManager();
            TypedQuery<Menu> query = em.createNamedQuery(Menu.findAll, Menu.class);
            List<Menu> menus = query.getResultList();
            System.out.println("getMenuEjb1:" + menus.size() + "-" + menu.hashCode() + "-" + menus.get(0).getMenuItems().toString());
            menu = menus.get(0);
            return menu;
        }
        System.out.println("getMenuEjb2:" + menu.toString());
        return menu;
    }

    @Override
    public void addMenu(Menu menu) {
        try {
//            EntityManager em = emf.createEntityManager();
            System.out.println("MenuEjb.addMenu" + menu.getMenuItems().size());
            em.persist(menu);
        } catch (SecurityException ex) {
            Logger.getLogger(MenuEjb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(MenuEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Menu updateMenu(Menu menu) {
        System.out.println("updateMenuEjb");
        menu = em.merge(menu);
        return menu;
    }
}


