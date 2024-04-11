import net.miginfocom.swing.MigLayout;
import javax.swing.*;

import java.awt.Component; 

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class OnlineShop extends JFrame {
    private JPanel contentPanel; 
    private Customer currentCustomer; // The currently logged-in customer

    
    public OnlineShop() {
        setTitle("Customer Registration and Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("fill", "", "[]push[]"));

        contentPanel = new JPanel(new MigLayout());
        add(contentPanel, "grow, push");

        showInitialButtons();
        
        pack();
        setLocationRelativeTo(null); 
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
    private void showAdminLogin(ActionEvent e) {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        contentPanel.add(new JLabel("Admin Login"), "span, grow, wrap");
        contentPanel.add(new JLabel("Username:"));
        contentPanel.add(usernameField, "growx");
        contentPanel.add(new JLabel("Password:"));
        contentPanel.add(passwordField, "growx");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e1 -> {
        	String username = usernameField.getText();
        	String password = new String(passwordField.getPassword());

        	adminLog adminLogger = new adminLog();
        	boolean authenticated = adminLogger.authenticateAdmin(username, password);
        	if (authenticated) {
        	    SessionManager.login(username, true); // true for admin
        	    // Proceed to admin dashboard or views
        	    navigateToAdminDashboard();
        	} else {
        	    JOptionPane.showMessageDialog(this, "Admin Login Failed. Please check your credentials and try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
        	}
        });

        contentPanel.add(loginButton, "span, growx");
        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }
    private void navigateToAdminDashboard() {
        new AdminDashboard(); // Create and display the admin dashboard
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
        	
        	String username = usernameField.getText();
        	String password = new String(passwordField.getPassword());

        	customerLog customerLogger = new customerLog();
        	boolean isAuthenticated = customerLogger.authenticateCust(username, password);
        	if (isAuthenticated) {
        	    SessionManager.login(username, false); 
        	    // Proceed to show customer views
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
    private final Basket basket = new Basket(); // Basket instance for the class
    private Map<JCheckBox, Item> itemCheckBoxes = new HashMap<>();
    private void showCatalog() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap", "[grow]", "[]10[]10[grow][]"));

        // Category and Manufacturer Dropdowns
        List<String> categories = dbHelper.fetchCategories();
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryComboBox.insertItemAt("All", 0);
        categoryComboBox.setSelectedIndex(0);

        List<String> manufacturers = dbHelper.fetchManufacturers();
        JComboBox<String> manufacturerComboBox = new JComboBox<>(manufacturers.toArray(new String[0]));
        manufacturerComboBox.insertItemAt("All", 0);
        manufacturerComboBox.setSelectedIndex(0);

        JComboBox<String> sortAttributeComboBox = new JComboBox<>(new String[]{"Title", "Price"});
        JComboBox<String> sortOrderComboBox = new JComboBox<>(new String[]{"Ascending", "Descending"});

        JButton searchButton = new JButton("Search");
        JButton viewBasketButton = new JButton("View Basket");
        JPanel itemsDisplayPanel = new JPanel(new MigLayout("wrap"));
        itemsDisplayPanel.setName("itemsDisplayPanel");

        contentPanel.add(new JLabel("Category: "), "split 2, span");
        contentPanel.add(categoryComboBox, "growx");
        contentPanel.add(new JLabel("Manufacturer: "), "split 2, span");
        contentPanel.add(manufacturerComboBox, "growx");

        contentPanel.add(new JLabel("Sort by: "), "split 2, span");
        contentPanel.add(sortAttributeComboBox, "growx");
        contentPanel.add(new JLabel("Order: "), "split 2, span");
        contentPanel.add(sortOrderComboBox, "growx");

        contentPanel.add(searchButton, "span, split 2, growx");
        contentPanel.add(viewBasketButton, "growx");
        contentPanel.add(itemsDisplayPanel, "grow, push");

        searchButton.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String selectedManufacturer = (String) manufacturerComboBox.getSelectedItem();
            String sortAttribute = (String) sortAttributeComboBox.getSelectedItem();
            String sortOrder = (String) sortOrderComboBox.getSelectedItem();
            displayItems(selectedCategory, selectedManufacturer, itemsDisplayPanel, sortAttribute, sortOrder);
        });

        viewBasketButton.addActionListener(e -> showBasketDialog());

        // Initial Display of Items
        displayItems("All", "All", itemsDisplayPanel, "Title", "Ascending");

        pack();
    }

    private void displayItems(String selectedCategory, String selectedManufacturer, JPanel itemsDisplayPanel, String sortAttribute, String sortOrder) {
        List<Item> items = dbHelper.fetchItemsByCategoryAndManufacturer(selectedCategory, selectedManufacturer);

        // Sort items based on the selected attribute and order
        items.sort((item1, item2) -> {
            int comparisonResult = 0;
            switch (sortAttribute) {
                case "Title":
                    comparisonResult = item1.getTitle().compareTo(item2.getTitle());
                    break;
                case "Manufacturer":
                    comparisonResult = item1.getManufacturer().compareTo(item2.getManufacturer());
                    break;
                case "Price":
                    comparisonResult = Double.compare(item1.getPrice(), item2.getPrice());
                    break;
            }
            return "Ascending".equals(sortOrder) ? comparisonResult : -comparisonResult;
        });

        itemsDisplayPanel.removeAll();
        itemCheckBoxes.clear();

        for (Item item : items) {
            JCheckBox checkBox = new JCheckBox(String.format("%s - $%.2f", item.getTitle(), item.getPrice()));

            checkBox.addActionListener(e -> {
                if (checkBox.isSelected()) {
                    basket.addItem(item); 
                } else {
                    basket.removeItem(item); 
                }
            });

            itemCheckBoxes.put(checkBox, item);
            itemsDisplayPanel.add(checkBox, "growx");
        }

        itemsDisplayPanel.revalidate();
        itemsDisplayPanel.repaint();
        pack();
    }

    private Customer getLoggedInCustomer() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            return null; 
        }
        customerLog customerLogger = new customerLog();
        return customerLogger.fetchCustomerDetails(currentUsername);
    }
    
    // Method to view and manage the basket
    private void showBasket() {
        Customer currentCustomer = getLoggedInCustomer();
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "No customer logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog basketDialog = new JDialog(this, "Your Basket", true);
        basketDialog.setLayout(new MigLayout("wrap 2"));

        if (basket.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(basketDialog, "Your basket is empty.", "Basket Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AtomicReference<Double> totalPrice = new AtomicReference<>(0.0); // Initialize total price

        for (Item item : basket.getItems()) {
            basketDialog.add(new JLabel(item.getTitle() + " - $" + item.getPrice()), "span, grow");
            totalPrice.updateAndGet(value -> value + item.getPrice()); // Accumulate item prices for total price
        }

        JLabel totalPriceLabel = new JLabel(String.format("Total Price: $%.2f", totalPrice.get()));
        basketDialog.add(totalPriceLabel, "span, growx");

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            basketDialog.setVisible(false);
            basketDialog.dispose();
            basket.clear(); 
            JOptionPane.showMessageDialog(this, "Checkout successful!");
        });
        basketDialog.add(checkoutButton, "span, grow");

        basketDialog.pack();
        basketDialog.setLocationRelativeTo(this);
        basketDialog.setVisible(true);
    }
   
    private void showBasketDialog() {
        Customer currentCustomer = getLoggedInCustomer();
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "No customer logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog basketDialog = new JDialog(this, "Basket", true);
        basketDialog.setLayout(new MigLayout("wrap 2"));

        if (basket.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(basketDialog, "Your basket is empty.", "Basket Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);

        for (Item item : basket.getItems()) {
            basketDialog.add(new JLabel(item.getTitle() + " - $" + item.getPrice()), "span, grow");
            totalPrice.updateAndGet(value -> value + item.getPrice());
        }

        JLabel totalPriceLabel = new JLabel(String.format("Total Price: $%.2f", totalPrice.get()));
        basketDialog.add(totalPriceLabel, "span, growx");

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            // Show the card details dialog
            showCardDetailsDialog(totalPrice.get(), basketDialog, basket.getItems());
        });

        basketDialog.add(checkoutButton, "span, growx");
        basketDialog.pack();
        basketDialog.setLocationRelativeTo(this);
        basketDialog.setVisible(true);
    }

    private void showCardDetailsDialog(double totalPrice, JDialog basketDialog, List<Item> items) {
        JDialog cardDetailsDialog = new JDialog(this, "Card Details", true);
        cardDetailsDialog.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

        JTextField cardNumberField = new JTextField(20);
        JTextField expiryDateField = new JTextField(5);
        JTextField cvcField = new JTextField(3);

        cardDetailsDialog.add(new JLabel("Card Number:"));
        cardDetailsDialog.add(cardNumberField, "growx");
        cardDetailsDialog.add(new JLabel("Expiry Date (MM/YY):"));
        cardDetailsDialog.add(expiryDateField, "growx");
        cardDetailsDialog.add(new JLabel("CVC:"));
        cardDetailsDialog.add(cvcField, "growx");

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            if (!validateCardDetails(cardNumberField.getText(), expiryDateField.getText(), cvcField.getText())) {
                JOptionPane.showMessageDialog(cardDetailsDialog, "Invalid card details. Please check your input.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer currentCustomer = getLoggedInCustomer();
            if (currentCustomer == null) {
            	JOptionPane.showMessageDialog(cardDetailsDialog, "Error: No customer is logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Order newOrder = new Order(0, items, currentCustomer, totalPrice, new Date());
                DBHelper dbHelper = new DBHelper();
                dbHelper.processOrder(newOrder); 
                JOptionPane.showMessageDialog(cardDetailsDialog, "Checkout Successful!");
                basket.clear(); 
                basketDialog.dispose(); 
                cardDetailsDialog.dispose(); 
                showReviewDialog(items);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(cardDetailsDialog, "Checkout failed: " + ex.getMessage(), "Checkout Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        cardDetailsDialog.add(submitButton, "span, growx");
        cardDetailsDialog.pack();
        cardDetailsDialog.setLocationRelativeTo(this);
        cardDetailsDialog.setVisible(true);
    }

    private boolean validateCardDetails(String cardNumber, String expiryDate, String cvc) {
        boolean isValidCardNumber = cardNumber.matches("\\d{16}");
        boolean isValidExpiryDate = expiryDate.matches("\\d{2}/\\d{2}");
        boolean isValidCvc = cvc.matches("\\d{3}");

        return isValidCardNumber && isValidExpiryDate && isValidCvc;
    }

    
    private void processOrder() throws SQLException {
        if (currentCustomer == null) {
            throw new IllegalStateException("No customer logged in.");
        }

        double totalPrice = basket.getTotalPrice(); 
        List<Item> items = basket.getItems();
        
        Date now = new Date();
        Order order = new Order(0, items, currentCustomer, totalPrice, now);
        
        DBHelper dbHelper = new DBHelper();
        dbHelper.processOrder(order);
        
        basket.clear(); 
        // Update UI accordingly
    }

    private void showReviewDialog(List<Item> purchasedItems) {
        // Ensure a customer is logged in before proceeding
        Customer currentCustomer = getLoggedInCustomer();
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to leave a review.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog reviewDialog = new JDialog(this, "Leave a Review", true);
        reviewDialog.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

        // Dropdown for purchased items
        JComboBox<Item> itemComboBox = new JComboBox<>(new Vector<>(purchasedItems));
        itemComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Item) {
                    setText(((Item) value).getTitle());
                }
                return this;
            }
        });

      
        JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        // Comment input
        JTextField commentField = new JTextField(20);

        reviewDialog.add(new JLabel("Item:"));
        reviewDialog.add(itemComboBox, "growx");
        reviewDialog.add(new JLabel("Rating:"));
        reviewDialog.add(ratingComboBox, "growx");
        reviewDialog.add(new JLabel("Comment:"));
        reviewDialog.add(commentField, "growx");

        JButton submitButton = new JButton("Submit Review");
        submitButton.addActionListener(e -> {
            Item selectedItem = (Item) itemComboBox.getSelectedItem();
            int rating = (Integer) ratingComboBox.getSelectedItem();
            String comment = commentField.getText();
            Review review = new Review(0, selectedItem, currentCustomer, rating, comment);
            
            try {
                new DBHelper().insertReview(review);
                JOptionPane.showMessageDialog(reviewDialog, "Thank you for your review!", "Review Submitted", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(reviewDialog, "Failed to submit review: " + ex.getMessage(), "Review Submission Error", JOptionPane.ERROR_MESSAGE);
            }

            reviewDialog.dispose();
        });

        reviewDialog.add(submitButton, "span, growx");
        reviewDialog.pack();
        reviewDialog.setLocationRelativeTo(this);
        reviewDialog.setVisible(true);
    }



	private void showItemDetails(int itemId) {
       
        JOptionPane.showMessageDialog(this, "Showing details for item ID: " + itemId);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineShop::new);
    }
}
