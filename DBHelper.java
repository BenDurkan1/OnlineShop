import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Ben?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // Make getConnection a static method for reuse
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public void insertItem(String title, String manufacturer, double price, String category, int quantity) {
        String sql = "INSERT INTO items (title, manufacturer, price, category, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, manufacturer);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setInt(5, quantity);
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
        // Removed the extra comma before WHERE
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
                                       rs.getString("category"), rs.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider replacing with more robust error handling
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
                                       rs.getString("category"), rs.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public void processOrder(Order order) throws SQLException {
        // Start a transaction
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // Deduct stock levels for each item in the order
                for (Item item : order.getItems()) {
                    updateItemStock(conn, item.getId(), -item.getQuantity()); // Assuming the quantity to deduct is stored in the item object
                }

                // Insert the order into the orders table and get the generated order ID
                int orderId = insertOrder(conn, order);

                // Insert each item into the order_items table with the generated order ID
                for (Item item : order.getItems()) {
                    insertOrderItem(conn, orderId, item.getId(), item.getQuantity());
                }

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on error
                throw e;
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit to true
            }
        }
    }
    private void updateItemStock(Connection conn, int itemId, int quantity) throws SQLException {
        String sql = "UPDATE items SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            pstmt.setInt(6, adminId); // Set the admin ID
            pstmt.setInt(7, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private int insertOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, total_price, order_date) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomer().getId());
            pstmt.setDouble(2, order.getTotalPrice());
            pstmt.setDate(3, new java.sql.Date(order.getDate().getTime()));
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
   


}


