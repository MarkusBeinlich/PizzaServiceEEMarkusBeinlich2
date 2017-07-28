/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.beinlich.markus.pizzaservice.controller;

import de.beinlich.markus.pizzaservice.model.Customer1;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;

/**
 *
 * @author schulung
 */
@Named
@javax.enterprise.context.SessionScoped
public class SigninController implements Serializable{

    private static final long serialVersionUID = 6839648856831788874L;

    private Customer1 cust;
    private String message;
    private Boolean loggedIn;
    private String username;

    public SigninController()  {
        this.message = "nicht eingeloggt";
        this.cust = new Customer1();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Customer1 getCust() {
        return cust;
    }

    public void setCust(Customer1 cust) {
        this.cust = cust;
    }

    public void doSomething() {

    }

    public boolean isLogin() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        System.out.println("isLogin as Admin:" + request.isUserInRole("admin") + 
                RequestContext.getCurrentInstance().getCallbackParams().get("loggedIn") );
        return request.isUserInRole("admin") || request.isUserInRole("customer");
    }

    public boolean isAdmin() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.isUserInRole("admin");
    }

    public String getUsername1() {
        if (isLogin()) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            return request.getUserPrincipal().getName();
        } else {
            return null;
        }
    }

    public String gotoLogin() {
        System.out.println("gotoLogin");
        return "toLogin";
    }

    public void login() {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage facesMessage = null;
        System.out.println("login with " + cust.getEmail() + cust.getPassword());
        loggedIn = false;
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            request.login(cust.getEmail(), cust.getPassword());
            loggedIn = true;
            username = cust.getEmail();
            facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", cust.getEmail());
        } catch (ServletException ex) {
            Logger.getLogger(SigninController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            context.addCallbackParam("loggedIn", loggedIn);
        }
        System.out.println("IsAdmin: " + isAdmin());
    }

    public Boolean getLoggedIn() {
        System.out.println("getLoggedIn: " + loggedIn);
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        System.out.println("getUsername: " + username);
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
