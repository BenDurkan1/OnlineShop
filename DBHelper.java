import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Ben?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private List<Observer> observers = new ArrayList<>();

    // Singleton instance
    private static DBHelper instance;

    // Private constructor to prevent instantiation from other classes
    private DBHelper() {}

    public static DBHelper getInstance() {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper();
                }
            }
        }
        return instance;
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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public void insertItem(String title, String manufacturer, double price, String category, int quantity, String imagePath) {
        String sql = "INSERT INTO items (title, manufacturer, price, category, quantity, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, manufacturer);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setInt(5, quantity);
            pstmt.setString(6, imagePath);
            pstmt.executeUpdate();
            System.out.println("Insert successful");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void readItems() {
        String sql = "SELECT * FROM items";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                                   rs.getString("title") + "\t" +
                                   rs.getString("manufacturer") + "\t" +
                                   rs.getDouble("price") + "\t" +
                                   rs.getString("category") + "\t" +
                                   rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateItem(int id, String title, String manufacturer, double price, String category, int quantity) {
        String sql = "UPDATE items SET title = ?, manufacturer = ?, price = ?, category = ?, quantity = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, manufacturer);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setInt(5, quantity);
            pstmt.setInt(6, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Item> searchItems(String query) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE title LIKE ? OR manufacturer LIKE ? OR category LIKE ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchQuery = "%" + query + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	items.add(new Item(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("manufacturer"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getString("image_path"))); // Include image path
                }
            }
        }
        return items;
    }
    public void correctNegativeItemQuantities() throws SQLException {
        String sql = "UPDATE items SET quantity = 0 WHERE quantity < 0";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Corrected " + affectedRows + " items with negative quantities.");
        }
    }

    public void deleteItem(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public List<Item> fetchItems(String searchQuery) {
        List<Item> items = new ArrayList<>();
        String baseSql = "SELECT * FROM items";
        String searchSql = " WHERE title LIKE ? OR manufacturer LIKE ? OR category LIKE ?";
        String finalSql = baseSql + (searchQuery != null && !searchQuery.trim().isEmpty() ? searchSql : "");
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(finalSql)) {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                pstmt.setString(1, "%" + searchQuery + "%");
                pstmt.setString(2, "%" + searchQuery + "%");
                pstmt.setString(3, "%" + searchQuery + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(rs.getInt("id"), rs.getString("title"),
                                       rs.getString("manufacturer"), rs.getDouble("price"),
                                       rs.getString("category"), rs.getInt("quantity"),
                                       rs.getString("image_path"))); // Include image path
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return items;
    }
    public static List<String> fetchCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM items ORDER BY category";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public static List<String> fetchManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        String sql = "SELECT DISTINCT manufacturer FROM items ORDER BY manufacturer";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                manufacturers.add(rs.getString("manufacturer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manufacturers;
    }
    
    public static List<Item> fetchItemsByCategoryAndManufacturer(String category, String manufacturer) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE (? = 'All' OR category = ?) AND (? = 'All' OR manufacturer = ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setString(2, category);
            pstmt.setString(3, manufacturer);
            pstmt.setString(4, manufacturer);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(rs.getInt("id"), rs.getString("title"),
                                       rs.getString("manufacturer"), rs.getDouble("price"),
                                       rs.getString("category"), rs.getInt("quantity"),
                                       rs.getString("image_path")));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    public void processOrder(Order order) throws SQLException {
        if (order == null || order.getCustomer() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order, customer, or items cannot be null or empty.");
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); 

            int orderId = insertOrder(conn, order);

            for (ItemInt item : order.getItems()) {
                insertOrderItem(conn, orderId, item.getId(), item.getQuantity());
            }

            conn.commit(); 
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback the transaction on error
            }
            throw e; 
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
            }
        }
    }
  
    private int insertOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, total_price, order_date) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomer().getId());
            pstmt.setDouble(2, order.getTotalPrice());
            pstmt.setTimestamp(3, new Timestamp(order.getDate().getTime()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated order ID
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    private void insertOrderItem(Connection conn, int orderId, int itemId, int quantity) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        }
    }



    public void updateItemStock(int itemId, int quantity) throws SQLException {
        String sql = "UPDATE items SET quantity = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        }
    }

    public void updateItem(int itemId, String title, String manufacturer, double price, String category, int quantity, int adminId) {
        String sql = "UPDATE items SET title = ?, manufacturer = ?, price = ?, category = ?, quantity = ?, last_updated_by_admin_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, manufacturer);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setInt(5, quantity);
            pstmt.setInt(6, adminId); 
            pstmt.setInt(7, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    
    public void insertReview(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (item_id, customer_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getItem().getId()); // Get ID from Item object
            pstmt.setInt(2, review.getCustomer().getId()); // Get ID from Customer object
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public List<Item> fetchAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY title"; 

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("manufacturer"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("image_path"))); 
            }
        }
        return items;
    }

    
    public List<Customer> getCustomerDetails() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, username, shipping_address, payment_method FROM customershop";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
           
                Customer customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("shipping_address"),
                    rs.getString("payment_method")
                );
                // Since password is not fetched, no need to set it here
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

    
    public List<Order> getCustomerOrders(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("id"),
                        customerId,
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date")
                    );
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    public List<Review> getCustomerReviews(int customerId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Fetch item and customer objects using their IDs
                    int reviewId = rs.getInt("id");
                    int itemId = rs.getInt("item_id");
                    int rating = rs.getInt("rating");
                    String comment = rs.getString("comment");

                    Item item = fetchItemById(itemId); 
                    Customer customer = fetchCustomerById(customerId);

                    // Create Review object and add it to the list
                    Review review = new Review(reviewId, item, customer, rating, comment);
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }
    public Item fetchItemById(int itemId) throws SQLException {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String manufacturer = rs.getString("manufacturer");
                    double price = rs.getDouble("price");
                    String category = rs.getString("category");
                    int quantity = rs.getInt("quantity");
                    String imagePath = rs.getString("image_path"); // This line was missing

                    return new Item(itemId, title, manufacturer, price, category, quantity, imagePath);
                }
            }
        }
        return null; 
    }


    public Customer fetchCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customershop WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String shippingAddress = rs.getString("shipping_address");
                    String paymentMethod = rs.getString("payment_method");

                    return new Customer(customerId, username, shippingAddress, paymentMethod);
                }
            }
        }
        return null; 
    }


}


