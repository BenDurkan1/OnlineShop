import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class AdminDashboard extends JFrame {
	 private JPanel contentPanel;
	    private JPanel controlPanel; // Panel for navigation buttons
	    private DBHelper dbHelper = new DBHelper();
	    public AdminDashboard() {
	        setTitle("Admin Dashboard");
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setLayout(new MigLayout("fill", "", "[]10[]push[]"));

	        setupControlPanel(); 

	        contentPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
	        add(contentPanel, "grow, push");

	        try {
	            dbHelper.correctNegativeItemQuantities();
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error correcting item quantities: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	        }

	        viewStockItems(); 

	        pack();
	        setLocationRelativeTo(null);
	        setVisible(true);
	    }
	  
	    private void setupControlPanel() {
	        JPanel controlPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
	        JButton viewItemsButton = new JButton("View Items");
	        viewItemsButton.addActionListener(e -> viewStockItems());
	        controlPanel.add(viewItemsButton, "growx");

	        JButton manageInventoryButton = new JButton("Manage Inventory");
	        manageInventoryButton.addActionListener(e -> setupSearchAndUpdateUI());
	        controlPanel.add(manageInventoryButton, "growx");

	        JButton viewCustomerDetailsButton = new JButton("View Customer Details");
	        viewCustomerDetailsButton.addActionListener(e -> viewCustomerDetails());
	        controlPanel.add(viewCustomerDetailsButton, "growx");

	        add(controlPanel, "dock north"); // Place the control panel at the top
	    }
	    private void viewCustomerDetails() {
	        System.out.println("Viewing customer details");
	        contentPanel.removeAll(); // Clear the previous view

	        List<Customer> customers;
	        try {
	            customers = dbHelper.getCustomerDetails();
	            for (Customer customer : customers) {
	                JPanel customerPanel = new JPanel(new MigLayout("wrap 3", "[grow]10[grow]10[grow]", "[]"));
	                String customerDetails = String.format(
	                    "<html>Username: %s<br>Shipping Address: %s<br>Payment Method: %s</html>",
	                    customer.getUsername(), customer.getShippingAddress(), customer.getPaymentMethod()
	                );
	                customerPanel.add(new JLabel(customerDetails));

	                // View Orders Button
	                JButton viewOrdersButton = new JButton("View Orders");
	                viewOrdersButton.addActionListener(e -> viewCustomerOrders(customer.getId())); 
	                customerPanel.add(viewOrdersButton, "growx");

	                // View Reviews Button
	                JButton viewReviewsButton = new JButton("View Reviews");
	             viewReviewsButton.addActionListener(e -> viewCustomerReviews(customer.getId())); 
	                customerPanel.add(viewReviewsButton, "growx");

	                contentPanel.add(customerPanel);
	            }
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error fetching customer details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        }
	        refreshContentPanel();
	    }

	    private void viewCustomerOrders(int customerId) {
	        contentPanel.removeAll(); 

	        try {
	            List<Order> orders = dbHelper.getCustomerOrders(customerId);
	            if (orders.isEmpty()) {
	                contentPanel.add(new JLabel("No orders found for this customer."));
	            } else {
	                // Iterate over each order and add details to pael
	                for (Order order : orders) {
	                    String orderDetails = String.format(
	                        "<html>Order ID: %d<br>Total Price: $%.2f<br>Order Date: %s</html>",
	                        order.getId(), order.getTotalPrice(), order.getDate().toString()
	                    );
	                    contentPanel.add(new JLabel(orderDetails), "wrap");
	                }
	            }
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error fetching customer orders: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        }
	        refreshContentPanel();
	    }


	    private void viewCustomerReviews(int customerId) {
	        contentPanel.removeAll(); // Clear the previous view

	        try {
	            List<Review> reviews = dbHelper.getCustomerReviews(customerId);
	            if (reviews.isEmpty()) {
	                contentPanel.add(new JLabel("No reviews found for this customer."));
	            } else {
	                for (Review review : reviews) {
	                    // Item associated with the review
	                    Item item = review.getItem();

	                    String reviewDetails = String.format(
	                        "<html>Item: %s<br>Rating: %d<br>Comment: %s</html>",
	                        item.getTitle(), review.getRating(), review.getComment()
	                    );
	                    contentPanel.add(new JLabel(reviewDetails), "wrap");
	                }
	            }
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error fetching customer reviews: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	            e.printStackTrace();
	        }
	        refreshContentPanel();
	    }



	    private void viewStockItems() {
	        System.out.println("Viewing all stock items");
	        contentPanel.removeAll(); 
	        List<Item> items = new ArrayList<>();
	        try {
	            items = dbHelper.fetchAllItems();
	            JPanel itemsPanel = new JPanel(new MigLayout("wrap 1", "[grow]", "[]"));

	            for (Item item : items) {
	                String filePath = item.getImagePath(); // Assuming direct path like 'C:\\Users\\bendu\\Downloads\\womensGraphicTee.jpg'
	                ImageIcon imageIcon = new ImageIcon(filePath);
	                JLabel imageLabel = new JLabel();

	                if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
	                    imageLabel.setText("Image load failed: " + filePath);
	                    System.out.println("Failed to load image: " + filePath);
	                } else {
	                    imageLabel.setIcon(imageIcon);
	                }

	                JLabel itemLabel = new JLabel(String.format(
	                    "<html>Title: %s<br>Manufacturer: %s<br>Price: $%.2f<br>Category: %s<br>Quantity: %d</html>",
	                    item.getTitle(), item.getManufacturer(), item.getPrice(), item.getCategory(), item.getQuantity()));

	                JPanel itemPanel = new JPanel(new MigLayout("wrap 2", "[grow]", "[]10[]"));
	                itemPanel.add(imageLabel);
	                itemPanel.add(itemLabel);
	                itemsPanel.add(itemPanel);
	            }

	            JScrollPane scrollPane = new JScrollPane(itemsPanel);
	            contentPanel.add(scrollPane, "push, grow, span");

	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error fetching stock items: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        }
	        refreshContentPanel();
	    }



	    private void refreshContentPanel() {
	        contentPanel.revalidate();
	        contentPanel.repaint();
	        System.out.println("Content panel refreshed");
	    }

	    
	    private void setupSearchAndUpdateUI() {
	        contentPanel.removeAll();
	        contentPanel.setLayout(new MigLayout("wrap 1", "[grow]", "[]10[]push[]"));

	        // Search field and button
	        JTextField searchField = new JTextField(20);
	        JButton searchButton = new JButton("Search");
	        searchButton.addActionListener(e -> {
	            String query = searchField.getText();
	            viewFilteredStockItems(query);
	        });

	        JPanel searchPanel = new JPanel(new MigLayout("wrap 2", "[grow]10[]", "[]"));
	        searchPanel.add(searchField, "grow");
	        searchPanel.add(searchButton);

	        JPanel searchAndItemsPanel = new JPanel(new MigLayout("wrap 1", "[grow]", "[]10[]push[]"));
	        searchAndItemsPanel.add(searchPanel, "grow, wrap");

	        JPanel itemsPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
	        JScrollPane scrollPane = new JScrollPane(itemsPanel);
	        searchAndItemsPanel.add(scrollPane, "grow, push");

	        // Viewport view of the scroll pane
	        scrollPane.setViewportView(itemsPanel);

	        contentPanel.add(searchAndItemsPanel, "grow, push");

	        // Display all items
	        viewFilteredStockItems("");
	    }
	 

	    private void viewFilteredStockItems(String query) {
	        System.out.println("Filtering stock items with query: " + query);
	        contentPanel.removeAll(); 
	        List<Item> items = new ArrayList<>();
	        try {
	            items = dbHelper.searchItems(query);
	            System.out.println("Fetched filtered items: " + items.size()); 
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Error fetching filtered items: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	            return; 
	        }

	        JPanel itemsPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));

	        for (Item item : items) {
	            String itemDetails = String.format("<html>Title: %s<br>Manufacturer: %s<br>Price: $%.2f<br>Category: %s<br>Quantity: %d</html>",
	                item.getTitle(), item.getManufacturer(), item.getPrice(), item.getCategory(), item.getQuantity());
	            JLabel itemLabel = new JLabel(itemDetails);

	            JTextField quantityField = new JTextField("0", 5); // Set text to 0
	            JButton updateButton = new JButton("Update To");
	            updateButton.addActionListener(e -> {
	                try {
	                    int newQuantity = Integer.parseInt(quantityField.getText());
	                    if (newQuantity <= 0) {
	                        JOptionPane.showMessageDialog(null, "Quantity must be a positive number greater than zero.");
	                        return; 
	                    }
	                    dbHelper.updateItemStock(item.getId(), newQuantity);
	                    JOptionPane.showMessageDialog(null, "Stock updated successfully!");
	                    viewFilteredStockItems(query); // Refresh the view
	                } catch (NumberFormatException | SQLException ex) {
	                    JOptionPane.showMessageDialog(null, "Error updating stock: " + ex.getMessage());
	                }
	            });
	            
	            JButton deleteButton = new JButton("Delete Item");
	            deleteButton.addActionListener(e -> {
	                dbHelper.deleteItem(item.getId());
	                JOptionPane.showMessageDialog(null, "Item deleted successfully!");
	                viewFilteredStockItems(query); 
	            });

	            
	            itemsPanel.add(itemLabel);
	            itemsPanel.add(quantityField);
	            itemsPanel.add(updateButton);
	            itemsPanel.add(deleteButton); 
	        }

	        JScrollPane scrollPane = new JScrollPane(itemsPanel);
	        contentPanel.add(scrollPane, "push, grow, span");
	        refreshContentPanel();
	    }


   


    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}