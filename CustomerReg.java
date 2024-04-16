import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerReg {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Ben?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public boolean registerCust(String username, String password, String shippingAddress, String paymentMethod) {
        // Check password requirements
        if (!isPasswordValid(password)) {
            System.err.println("Password does not meet requirements.");
            return false;
        }

        String sql = "INSERT INTO CustomerShop (username, password, shipping_address, payment_method) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, shippingAddress);
            preparedStatement.setString(4, paymentMethod);

            // Executing the SQL query to insert the customer
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Registration failed
        }
    }

    // Method to check if the password meets the requirements
    private boolean isPasswordValid(String password) {
        // Check length
        if (password.length() < 8) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (!hasUppercase) {
            return false;
        }
        // Check for at least one special character
        String specialChars = "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
        boolean hasSpecialChar = false;
        for (char c : specialChars.toCharArray()) {
            if (password.indexOf(c) != -1) {
                hasSpecialChar = true;
                break;
            }
        }
        return hasSpecialChar;
    }
}