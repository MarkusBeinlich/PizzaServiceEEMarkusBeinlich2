package de.beinlich.markus.pizzaservice.controller;

import de.beinlich.markus.pizzaservice.ejb.CustomerEjbRemote;
import de.beinlich.markus.pizzaservice.ejb.MenuEjbRemote;
import de.beinlich.markus.pizzaservice.ejb.OrderEjbRemote;
import de.beinlich.markus.pizzaservice.ejb.OrderEvent;
import de.beinlich.markus.pizzaservice.model.Customer;
import de.beinlich.markus.pizzaservice.model.Invoice;
import de.beinlich.markus.pizzaservice.model.Menu;
import de.beinlich.markus.pizzaservice.model.MenuItem;
import de.beinlich.markus.pizzaservice.model.OrderEntry;
import de.beinlich.markus.pizzaservice.model.OrderHeader;
import de.beinlich.markus.pizzaservice.util.ActiveSessionsListener;
import java.io.Serializable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Markus Beinlich
 */
@Named
@SessionScoped
public class OrderPizza implements Serializable {

    private final CustomerEjbRemote customerEjb = lookupCustomerEjbRemote();

    private final OrderEjbRemote orderEjb = lookupOrderEjbRemote();

    private final MenuEjbRemote menuEjb = lookupMenuEjbRemote();

    private static final long serialVersionUID = 4711892445353241012L;

    private Customer customer;
    private Invoice invoice;
    private OrderHeader order;
    private String time;
    private Menu menu;
    private Boolean submitted;
    private MenuItem newMenuItem;
    private MenuItem selectedMenuItem;

    private enum OrderStatus {
        MENU, CUSTOMER, CONFIRMATION
    };
    private OrderStatus orderStatus;

    public OrderPizza() {

    }

    @PostConstruct
    private void initOrderPizza() {
        System.out.println("@PostConstruct OrderPizza");
        customer = new Customer();
        invoice = new Invoice();
        order = new OrderHeader();
        menu = new Menu();
        submitted = false;
        newMenuItem = new MenuItem();
        orderStatus = OrderStatus.MENU;
    }

    private CustomerEjbRemote lookupCustomerEjbRemote() {
        try {
            Context c = new InitialContext();
            return (CustomerEjbRemote) c.lookup("ejb/customerEjb");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private OrderEjbRemote lookupOrderEjbRemote() {
        try {
            Context c = new InitialContext();
            return (OrderEjbRemote) c.lookup("ejb/orderEjb");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private MenuEjbRemote lookupMenuEjbRemote() {
        try {
            Context c = new InitialContext();
            return (MenuEjbRemote) c.lookup("ejb/menuEjb");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    // DAs geht leider nicht, da das Event ja in einer anderen JVM ausgeführt wird
//    public void getNewOrderHeader(@Observes OrderEvent orderEvent) {
//        System.out.println("getNewOrderHeader - OrderPizza");
//        customer.getOrderHeaders().add(orderEvent.getOrder());
//    }
    public void deleteMenuItem(MenuItem menuItem) {
//        System.out.println("delete:" + selectedMenuItem.getName());
//        menu.getMenuItems().remove(selectedMenuItem);
        menu.getMenuItems().remove(menuItem);
//        selectedMenuItem = null;
    }

    public void submitOrder() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        order.setIpAddress(req.getLocalAddr());
        order.setSessionId(req.getSession().getId());
        System.out.println("OrderPizza - save");
        System.out.println("OrderPizza.save: ip-" + order.getIpAddress() + " session: " + order.getSessionId());
//            customer.store();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        this.saveJms();
        submitted = true;
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vielen Dank für Ihre Bestellung", "Guten Appetit.");
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }
    @Inject
    @JMSConnectionFactory("jms/myConnectionFactory")
    private JMSContext context;

    @Resource(lookup = "jms/PizzaOrderQueue")
    Queue pizzaOrderQueue;

    public void saveJms() {

        JMSProducer producer = context.createProducer();
        ObjectMessage objectMessage = context.createObjectMessage();
        try {
            objectMessage.setObject(order);
            producer.send(pizzaOrderQueue, objectMessage);
        } catch (JMSException ex) {
            Logger.getLogger(OrderPizza.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void save() {
        orderEjb.saveOrder(order);
    }

    public void showPdf() {
        try {
//            RequestContext.getCurrentInstance().getApplicationContext().
//            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            System.out.println("showPdf context: " + context);
            context.redirect("generate/myPdf.pdf");
        } catch (IOException ex) {
            Logger.getLogger(OrderPizza.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addOrderEntry() {
        System.out.println("addOrderEntry" + menu.getMenuItems().size());
        order.getOrderEntries().clear();
        for (MenuItem menuItem : menu.getMenuItems()) {
            if (menuItem.getQuantity() != 0) {
                order.addOrderEntry(new OrderEntry(menuItem));
                System.out.println("MenuItem <> 0:" + menuItem.getName());
            }
        }
    }

    @Inject
    SigninController signinController;

    public void listenToLogin(@Observes LoginEvent loginEvent) {
        System.out.println("listenToLogin");
        this.customer = getCustomerByEmail();
        System.out.println("listenToLogin2");
    }

    public String startOrder() {
        System.out.println("startOrder");
        if (customer.getEmail() == null) {
            orderStatus = OrderStatus.CUSTOMER;
            return "toCustomer";
        } else {
            orderStatus = OrderStatus.CONFIRMATION;
            return "toConfirmation";
        }

    }

    public String enterCustomer() {
        System.out.println("enterCustomer1");
        return "toCustomer";
    }

    public String customerEntered() {
        switch (orderStatus) {
            case CONFIRMATION:
                return "toConfirmation";
            case MENU:
                return "toMenu";
            default:
                return "toConfirmation";
        }
    }

    public void setIpAndSession(HttpServletRequest req) {
        order.setIpAddress(req.getRemoteAddr());
        HttpSession sess = req.getSession();
        order.setSessionId(sess.getId());
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Invoice getInvoice() {
        return new Invoice(customer, order);
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public OrderHeader getOrder() {
        System.out.println("getOrder");
        return order;
    }

    public void setOrder(OrderHeader order) {
        System.out.println("setOrder");
        this.order = order;
    }

    public String getSessionId() {
        return order.getSessionId();
    }

    public void setSessionId(String sessionId) {
        order.setSessionId(sessionId);
    }

    public String getIpAddress() {
        return order.getIpAddress();
    }

    public void setIpAddress(String ipAddress) {
        order.setIpAddress(ipAddress);
    }

    public String getTime() {
        return "a" + String.valueOf(System.currentTimeMillis()) + "x";
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Customer getCustomerByEmail() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Customer customerDb;
        customerDb = customerEjb.getCustomerByEmail(request.getUserPrincipal().getName());
        if (customerDb != null) {
            this.customer = customerDb;
            System.out.println("customerDb <> null");
        }
        System.out.println("getCustomer:" + request.getUserPrincipal().getName() + " - " + customer.getLastName() + " - " + customer.getOrderHeaders().size());
        return customer;
    }

    public Menu getMenu() {
        if (menu.getMenuItems().isEmpty()) {
            menu = menuEjb.getMenu(menu);
        }
        System.out.println("getMenu2:" + menu.toString());
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void addMenu() {
        System.out.println("addMenu" + menu.getMenuItems().size());
        menuEjb.addMenu(menu);
        showMessage("Die Speisekarte wurde gespeichert.");
    }

    public void updateMenu() {
        System.out.println("updateMenu" + menu.getMenuItems().size());
        menu = menuEjb.updateMenu(menu);
        showMessage("Die Speisekarte wurde gespeichert.");
    }

    public void addMenuItem() {
        System.out.println("addMenuItem:" + newMenuItem.getName());
        newMenuItem.setMenuItemId(menu.getMenuItems().get(menu.getMenuItems().size() - 1).getMenuItemId() + 1);
        newMenuItem.setMenu(menu);
        menu.getMenuItems().add(newMenuItem);
        newMenuItem = new MenuItem();
    }

    public String initMenu() {
        menu = new Menu();
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Pizza1");
        menuItem.setDescription("Salami, Tomaten, Mozarella");
        menuItem.setPrice(new BigDecimal(7.5));
        menuItem.setMenu(menu);
        menu.getMenuItems().add(menuItem);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setName("Pizza2");
        menuItem2.setDescription("Salami, Schinken, Mozarella");
        menuItem2.setPrice(new BigDecimal(8.5));
        menuItem2.setMenu(menu);
        menu.getMenuItems().add(menuItem2);

        MenuItem menuItem3 = new MenuItem();

        menuItem3.setName("Pizza3");
        menuItem3.setDescription("Tomaten, Mozarella");
        menuItem3.setPrice(new BigDecimal(4.5));
        menuItem3.setMenu(menu);
        menu.getMenuItems().add(menuItem3);

        System.out.println("initMenu" + menu);
        this.menu = menu;
        addMenu();
        return ("toAdmin");
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Collection<HttpSession> getActiveSessionsAsCollection() {
        Collection<HttpSession> sessions = ActiveSessionsListener.getActiveSessions().values();
        return sessions;
    }

    public MenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public void setNewMenuItem(MenuItem newMenuItem) {
        this.newMenuItem = newMenuItem;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    public void showMessage(String text) {
        FacesMessage message = new FacesMessage(text);

        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
