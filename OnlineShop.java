  import net.miginfocom.swing.MigLayout;
import javax.swing.*;

import java.awt.Component; 

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class OnlineShop extends JFrame {
    private JPanel contentPanel; 
    private Customer currentCustomer; 
    private final DataModel dataModel; // Instance of DataModel for managing data
    private static final SortingStrategy TITLE_SORTING_STRATEGY = new TitleSortingStrategy();
    private static final SortingStrategy PRICE_SORTING_STRATEGY = new PriceSortingStrategy();
    private SortingStrategy sortingStrategy; // Declare sortingStrategy field
    private final Basket basket = new Basket(); // Basket instance for the class

    public OnlineShop() {

        this.dataModel = new DataModel();
		setTitle("Online Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("fill", "", "[]push[]"));

        contentPanel = new JPanel(new MigLayout());
        add(contentPanel, "grow, push");
        sortingStrategy = TITLE_SORTING_STRATEGY;

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
            	// Factory Method
                Customer newCustomer = CustomerFactory.createCustomer(0, usernameField.getText(), shippingAddressField.getText(), paymentMethodField.getText());

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
        // Create and display the admin dashboard with the DataModel instance
        AdminDashboard adminDashboard = new AdminDashboard(dataModel);
        adminDashboard.setVisible(true);
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


   



    DBHelper dbHelper = DBHelper.getInstance();
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

            System.out.println("Sort Attribute: " + sortAttribute); // Debug print

            if ("Title".equals(sortAttribute)) {
                sortingStrategy = new TitleSortingStrategy();
            } else if ("Price".equals(sortAttribute)) {
                sortingStrategy = new PriceSortingStrategy();
            } else {
                sortingStrategy = TITLE_SORTING_STRATEGY; // Default 
            }

            displayItems(selectedCategory, selectedManufacturer, itemsDisplayPanel, sortingStrategy, sortOrder);
        });

        
        viewBasketButton.addActionListener(e -> showBasketDialog());

        
        displayItems("All", "All", itemsDisplayPanel, null, "Ascending"); 

        pack();
        }

    private JLabel imageLabel;

    private void displayItems(String selectedCategory, String selectedManufacturer, JPanel itemsDisplayPanel,
            SortingStrategy sortingStrategy, String sortOrder) {
List<Item> rawItems = dbHelper.fetchItemsByCategoryAndManufacturer(selectedCategory, selectedManufacturer);
List<ItemInt> items = new ArrayList<>();

for (Item item : rawItems) {
items.add(new DiscountDecorator(item, 10)); // Assume 10% discount for all items for simplicity
}

// Check if sorting strategy is null and set default if necessary
if (sortingStrategy == null) {
sortingStrategy = TITLE_SORTING_STRATEGY; // Default to title sorting if none is set
}

sortingStrategy.sort(items);
if ("Descending".equals(sortOrder)) {
Collections.reverse(items);
}


itemCheckBoxes.clear();
JPanel itemPanelContainer = new JPanel(new MigLayout("wrap 2", "[grow]", "[]10[]"));

for (ItemInt decoratedItem : items) {
Item actualItem = ((ItemDecorator)decoratedItem).getDecoratedItem();  

JLabel itemLabel = new JLabel(decoratedItem.getDescription());
JLabel imageLabel = new JLabel();
try {
File imgFile = new File(actualItem.getImagePath());
ImageIcon imageIcon = new ImageIcon(imgFile.toURI().toURL());
imageLabel.setIcon(imageIcon);
} catch (MalformedURLException ex) {
imageLabel.setText("Image load failed");
System.out.println("Failed to load image: " + actualItem.getImagePath());
}

JPanel itemPanel = new JPanel(new MigLayout("wrap 2", "[grow]", "[]10[]"));
itemPanel.add(imageLabel, "spany, grow");
itemPanel.add(itemLabel, "wrap");

JCheckBox checkBox = new JCheckBox(String.format("Buy at $%.2f", decoratedItem.getPrice()));
checkBox.addActionListener(e -> {
if (checkBox.isSelected()) {
  basket.addItem(actualItem);
  try {
      dbHelper.updateItemStock(actualItem.getId(), actualItem.getQuantity() - 1);
  } catch (SQLException ex) {
      ex.printStackTrace();
  }
} else {
  basket.removeItem(actualItem);
}
});

itemCheckBoxes.put(checkBox, actualItem);
itemPanel.add(checkBox, "growx");
itemPanelContainer.add(itemPanel, "growx");
}

JScrollPane scrollPane = new JScrollPane(itemPanelContainer);
itemsDisplayPanel.removeAll();
itemsDisplayPanel.add(scrollPane, "grow, push");
itemsDisplayPanel.revalidate();
itemsDisplayPanel.repaint();
pack();
}


    public void addItemToBasket(Item item) {
        ItemInt decoratedItem = new DiscountDecorator(item, 10);  
        System.out.println("Adding to basket with discounted price: " + decoratedItem.getPrice());
        basket.addItem(decoratedItem);
    }

    private Customer getLoggedInCustomer() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            return null; 
        }
        customerLog customerLogger = new customerLog();
        return customerLogger.fetchCustomerDetails(currentUsername);
    }
    

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

        AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);

        for (ItemInt item : basket.getItems()) {
            // Apply discount decorator when displaying items
            ItemInt discountedItem = new DiscountDecorator((Item) item, 10);
            basketDialog.add(new JLabel(discountedItem.getDescription() + " - $" + String.format("%.2f", discountedItem.getPrice())), "span, grow");
            totalPrice.updateAndGet(value -> value + discountedItem.getPrice());
        }

        JLabel totalPriceLabel = new JLabel(String.format("Total Price: $%.2f", totalPrice.get()));
        basketDialog.add(totalPriceLabel, "span, growx");

        JButton checkoutButton = new JButton("Checkout");
        double finalTotalPrice = totalPrice.get(); 
        checkoutButton.addActionListener(e -> {
            basketDialog.setVisible(false);
            basketDialog.dispose();
            showCardDetailsDialog(finalTotalPrice, basketDialog, basket.getItems());
        });
        basketDialog.add(checkoutButton, "span, grow");

        basketDialog.pack();
        basketDialog.setLocationRelativeTo(this);
        basketDialog.setVisible(true);
    }

    private void showBasketDialog() {
        Customer currentCustomer = getLoggedInCustomer();
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Your basket is empty.", "Basket Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<ItemInt> items = basket.getItems();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your basket is empty.", "Basket Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog basketDialog = new JDialog(this, "Basket", true);
        basketDialog.setLayout(new MigLayout("wrap 2"));

        double totalPrice = 0.0; // Initialize total price variable

        for (ItemInt item : items) {
            // Apply discount decorator when displaying items
            ItemInt discountedItem = new DiscountDecorator((Item) item, 10); 
            String itemDescription = discountedItem.getDescription() + " - $" + String.format("%.2f", discountedItem.getPrice());
            basketDialog.add(new JLabel(itemDescription), "span, grow");
            totalPrice += discountedItem.getPrice(); // Update total price
        }

        JLabel totalPriceLabel = new JLabel(String.format("Total Price: $%.2f", totalPrice));
        basketDialog.add(totalPriceLabel, "span, growx");

        JButton checkoutButton = new JButton("Checkout");
        double finalTotalPrice = totalPrice; //  totalPrice in a final variable
        checkoutButton.addActionListener(e -> {
            basketDialog.dispose();
            showCardDetailsDialog(finalTotalPrice, basketDialog, items);
        });
        basketDialog.add(checkoutButton, "span, growx");
        basketDialog.pack();
        basketDialog.setLocationRelativeTo(this);
        basketDialog.setVisible(true);
    }


    private void showCardDetailsDialog(double totalPrice, JDialog basketDialog, List<ItemInt> items) {
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
                DBHelper dbHelper = DBHelper.getInstance();
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
        boolean isValidCvc = cvc.matches("\\d{3}");

        // Check if the card number and CVC are valid
        if (!isValidCardNumber || !isValidCvc) {
            return false;
        }

        
        boolean isValidExpiryDate = expiryDate.matches("\\d{2}/\\d{2}");
        if (!isValidExpiryDate) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            sdf.setLenient(false);
            Date expiry = sdf.parse(expiryDate);
            boolean isExpired = expiry.before(new Date());
            return !isExpired;
        } catch (ParseException e) {
            return false; // If the date can't be parsed, return false
        }
    }

    
    	private void processOrder() throws SQLException {
    	    if (currentCustomer == null) {
    	        throw new IllegalStateException("No customer logged in.");
    	    }

    	    double totalPrice = basket.getTotalPrice(); 
    	    List<ItemInt> items = basket.getItems();
    	    
    	    Date now = new Date();
    	    Order newOrder = OrderFactory.createOrder(0, items, currentCustomer, totalPrice, now);
    	    dbHelper.processOrder(newOrder);
    	    
    	    basket.clear(); 
    	}


    private void showReviewDialog(List<ItemInt> items) {
        Customer currentCustomer = getLoggedInCustomer();
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to leave a review.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        for (ItemInt selectedItem : items) {
            JDialog reviewDialog = new JDialog(this, "Leave a Review", true);
            reviewDialog.setLayout(new MigLayout("wrap 2", "[align right]10[align left, grow]", "[]10[]"));

            JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
            
            // Comment input
            JTextField commentField = new JTextField(20);

            reviewDialog.add(new JLabel("Item:"));
            reviewDialog.add(new JLabel(selectedItem.getTitle()), "growx");
            reviewDialog.add(new JLabel("Rating:"));
            reviewDialog.add(ratingComboBox, "growx");
            reviewDialog.add(new JLabel("Comment:"));
            reviewDialog.add(commentField, "growx");

            JButton submitButton = new JButton("Submit Review");
            submitButton.addActionListener(e -> {
                int rating = (Integer) ratingComboBox.getSelectedItem();
                String comment = commentField.getText();
                Review review = ReviewFactory.createReview(0, selectedItem, currentCustomer, rating, comment);
                
                try {
                	DBHelper.getInstance().insertReview(review);
                    JOptionPane.showMessageDialog(reviewDialog, "Thank you for your review!", "Review Submitted", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(reviewDialog, "Failed to submit review: " + ex.getMessage(), "Review Submission Error", JOptionPane.ERROR_MESSAGE);
                }

                reviewDialog.dispose();
            });

            reviewDialog.add(submitButton, "span, growx");
            reviewDialog.pack();
            reviewDialog.setLocationRelativeTo(this);
            reviewDialog.setVisible(true);
        }
    }



	private void showItemDetails(int itemId) {
       
        JOptionPane.showMessageDialog(this, "Showing details for item ID: " + itemId);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineShop::new);
    }
}
