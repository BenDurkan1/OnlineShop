import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class  adminLog {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Ben?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public boolean authenticateAdmin(String username, String password) {
        String sql = "SELECT COUNT(*) FROM admin WHERE username = ? AND password = ?";
        
        try (
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
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
    
   
    
}
