import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private List<Observer> observers = new ArrayList<>();
    private DBHelper dbHelper;

    public DataModel() {
        this.dbHelper = DBHelper.getInstance();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public List<Customer> getCustomerDetails() throws SQLException {
        return dbHelper.getCustomerDetails();  
    }

    public List<Order> getCustomerOrders(int customerId) throws SQLException {
        return dbHelper.getCustomerOrders(customerId); 
    }

    public List<Review> getCustomerReviews(int customerId) throws SQLException {
        return dbHelper.getCustomerReviews(customerId);  
    }

    public List<Item> getAllItems() throws SQLException {
        return dbHelper.fetchAllItems();  
    }

    public List<Item> searchItems(String query) throws SQLException {
        return dbHelper.searchItems(query);
    }

    public void updateItemStock(int itemId, int quantity) throws SQLException {
        dbHelper.updateItemStock(itemId, quantity);
        notifyObservers("Stock updated for item ID: " + itemId);
    }

    public void deleteItem(int itemId) throws SQLException {
        dbHelper.deleteItem(itemId);
        notifyObservers("Item deleted: " + itemId);
    }

    public void changeData(String data) {
        // Simulate a change in data
        notifyObservers("Data updated: " + data);
    }
}
