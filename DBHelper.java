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


}


