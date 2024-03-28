import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class  customerLog {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Ben?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public boolean authenticateCust(String username, String password) {
        String sql = "SELECT COUNT(*) FROM CustomerShop WHERE username = ? AND password = ?";
        
        try (
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            // Setting parameters for the prepared statement
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            // Executing the SQL query to retrieve customer credentials
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // If a record with matching credentials is found, return true
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true; // Authentication successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Authentication failed
    }
    
    public String getCustomerUsername(String username) {
        String sql = "SELECT username FROM CustomerShop WHERE username = ?";
        try (
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username"); // Return the username
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found or error
    }
    public Customer fetchCustomerDetails(String username) {
        String sql = "SELECT id, username, password, shipping_address, payment_method FROM CustomerShop WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String user = resultSet.getString("username");
                String shippingAddress = resultSet.getString("shipping_address"); // If the column exists
                String paymentMethod = resultSet.getString("payment_method");
                // Return a new Customer object, adjust constructor as needed
                return new Customer(id, user, shippingAddress,  paymentMethod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found or error
    }

    
}
