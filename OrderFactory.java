import java.util.Date;
import java.util.List;

public class OrderFactory {
    public static Order createOrder(int id, List<ItemInt> items, Customer customer, double totalPrice, Date date) {
        return new Order(id, items, customer, totalPrice, date);
    }
}