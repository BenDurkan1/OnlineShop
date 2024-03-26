import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private List<Item> items;
    private Customer customer;
    private double totalPrice;
    private Date date;

    // Constructor
    public Order(int id, List<Item> items, Customer customer, double totalPrice, Date date) {
        this.id = id;
        this.items = items;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
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
