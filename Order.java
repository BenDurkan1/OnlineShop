
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private List<ItemInt> items;
    private Customer customer;
    private double totalPrice;
    private Date date;

    // Constructor
    public Order(int id, List<ItemInt> items, Customer customer, double totalPrice, Date date) {
        this.id = id;
        this.items = items;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.date = date;
    }
    public Order(int id, int customerId, double totalPrice, Date orderDate) {
        this.id = id;
        this.customer = new Customer(customerId, null, null, null); // Simplified, adjust as needed
        this.totalPrice = totalPrice;
        this.date = orderDate;
        this.items = new ArrayList<>(); 
    }
    

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ItemInt> getItems() {
        return items;
    }

    public void setItems(List<ItemInt> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
