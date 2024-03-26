import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnlineShop extends JFrame {
    private JPanel contentPanel; // Panel to dynamically update content

    public OnlineShop() {
        setTitle("Customer Registration and Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("fill", "", "[]push[]"));

        contentPanel = new JPanel(new MigLayout());
        add(contentPanel, "grow, push");

        showInitialButtons();
        
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void showInitialButtons() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 1", "[center]", "[]15[]"));

        JButton adminButton = new JButton("Admin");
        JButton customerButton = new JButton("Customer");

        adminButton.addActionListener(this::showAdminLogin);
        customerButton.addActionListener(this::showCustomerLogin);

        contentPanel.add(adminButton, "growx");
        contentPanel.add(customerButton, "growx");

        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }

    private void showAdminLogin(ActionEvent e) {
        setupLoginPanel("Admin Login", false);
    }

    private void showCustomerLogin(ActionEvent e) {
        setupLoginPanel("Customer Login", true);
    }
    
    private void showRegistrationForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

        contentPanel.add(new JLabel("Registration"), "span, grow, wrap");
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField shippingAddressField = new JTextField(15);
        JTextField paymentMethodField = new JTextField(15);
        
        contentPanel.add(new JLabel("Username:"));
        contentPanel.add(usernameField, "growx");

        contentPanel.add(new JLabel("Password:"));
        contentPanel.add(passwordField, "growx");

        contentPanel.add(new JLabel("Shipping Address:"));
        contentPanel.add(shippingAddressField, "growx");

        contentPanel.add(new JLabel("Payment Method:"));
        contentPanel.add(paymentMethodField, "growx");

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            CustomerReg customerReg = new CustomerReg();
            try {
                boolean success = customerReg.registerCust(
                    usernameField.getText(), 
                    new String(passwordField.getPassword()), 
                    shippingAddressField.getText(), 
                    paymentMethodField.getText()
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration Successful.");
                    showCustomerLogin(null); // Switch back to the login form after successful registration
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed. Please check your input and try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Password Requirements Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPanel.add(submitButton, "span, growx");

        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }

    private void setupLoginPanel(String title, boolean showRegister) {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        
        contentPanel.add(new JLabel(title), "span, grow, wrap");
        contentPanel.add(new JLabel("Username:"));
        contentPanel.add(usernameField, "growx");

        contentPanel.add(new JLabel("Password:"));
        contentPanel.add(passwordField, "growx");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            customerLog customerLog = new customerLog();
            boolean isAuthenticated = customerLog.authenticateCust(
                usernameField.getText(), 
                new String(passwordField.getPassword())
            );

            if (isAuthenticated) {
                JOptionPane.showMessageDialog(this, "Login Successful.");
                // Transition to next part of application
                showCatalog();
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed. Please check your credentials and try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPanel.add(loginButton, "span, growx");

        if (showRegister) {
            JButton registerButton = new JButton("Register");
            registerButton.addActionListener(e -> showRegistrationForm());
            contentPanel.add(registerButton, "span, growx");
        }

        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }


    private DBHelper dbHelper = new DBHelper();

    private void showCatalog() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap", "[grow]", "[]10[grow]"));

        List<String> categories = dbHelper.fetchCategories();
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryComboBox.insertItemAt("All", 0);
        categoryComboBox.setSelectedIndex(0);

        List<String> manufacturers = dbHelper.fetchManufacturers();
        JComboBox<String> manufacturerComboBox = new JComboBox<>(manufacturers.toArray(new String[0]));
        manufacturerComboBox.insertItemAt("All", 0);
        manufacturerComboBox.setSelectedIndex(0);

        JButton searchButton = new JButton("Search");
        JPanel itemsDisplayPanel = new JPanel(new MigLayout("wrap")); // Dedicated for items
        itemsDisplayPanel.setName("itemsDisplayPanel"); // Assign a name for easy identification

        // Add components to contentPanel
        contentPanel.add(new JLabel("Category: "), "split 2, span");
        contentPanel.add(categoryComboBox, "growx");
        contentPanel.add(new JLabel("Manufacturer: "), "split 2, span");
        contentPanel.add(manufacturerComboBox, "growx");
        contentPanel.add(searchButton, "wrap");
        contentPanel.add(itemsDisplayPanel, "grow, push");

        searchButton.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String selectedManufacturer = (String) manufacturerComboBox.getSelectedItem();
            displayItems(selectedCategory, selectedManufacturer, itemsDisplayPanel);
        });

        displayItems("All", "All", itemsDisplayPanel);

        pack();
    }

    private void displayItems(String selectedCategory, String selectedManufacturer, JPanel itemsDisplayPanel) {
        List<Item> items = dbHelper.fetchItemsByCategoryAndManufacturer(selectedCategory, selectedManufacturer);
        itemsDisplayPanel.removeAll();

        for (Item item : items) {
            JLabel itemLabel = new JLabel(item.getTitle());
            itemsDisplayPanel.add(itemLabel, "growx");
        }

        itemsDisplayPanel.revalidate();
        itemsDisplayPanel.repaint();
        pack(); // Ensure the JFrame is correctly sized to accommodate the new items
    }

    public List<Item> fetchItemsByCategoryAndManufacturer(String category, String manufacturer) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT * FROM items WHERE (? = 'All' OR category = ?) AND (? = 'All' OR manufacturer = ?)";
        
        try (Connection conn = DBHelper.getConnection();

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


	
    
  
    private void showItemDetails(int itemId) {
        // Here, you would fetch the item details using the item ID
        // Then display those details in a new panel or dialog
        JOptionPane.showMessageDialog(this, "Showing details for item ID: " + itemId);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineShop::new);
    }
}
