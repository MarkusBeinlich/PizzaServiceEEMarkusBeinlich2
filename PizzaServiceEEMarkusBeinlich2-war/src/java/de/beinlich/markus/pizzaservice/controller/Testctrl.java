/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author Markus Beinlich
 */
@Named(value = "testctrl")
@SessionScoped
public class Testctrl implements Serializable {

    private String test;
    /**
     * Creates a new instance of Testctrl
     */
    public Testctrl() {
        test="Hallo Welt";
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
    
}
