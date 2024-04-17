import javax.print.DocFlavor.URL;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.MediaTracker;

public class AdminDashboard extends JFrame implements Observer {
    private JPanel contentPanel;
    private JTextField searchField;

    private DataModel dataModel;

    public AdminDashboard(DataModel dataModel) {
        this.dataModel = dataModel;
        this.dataModel.addObserver(this);
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("fill", "", "[]10[]push[]"));

        setupControlPanel();
        contentPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
        add(contentPanel, "grow, push");

        viewStockItems();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupControlPanel() {
        JPanel controlPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
        JButton viewItemsButton = new JButton("View Items");
        viewItemsButton.addActionListener(e -> {
            viewStockItems();
        });
        controlPanel.add(viewItemsButton, "growx");

        JButton viewCustomersButton = new JButton("View Customer Details");
        viewCustomersButton.addActionListener(e -> {
            try {
                viewCustomerDetails();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading customer details.", "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        controlPanel.add(viewCustomersButton, "growx");

        searchField = new JTextField(20);
        JButton filterButton = new JButton("Filter Items");
        filterButton.addActionListener(e -> {
            try {
                viewFilteredStockItems(searchField.getText());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error filtering stock items.", "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        controlPanel.add(searchField, "growx");
        controlPanel.add(filterButton, "growx");

        add(controlPanel, "dock north");
    }

    private void viewStockItems() {
        contentPanel.removeAll();
        List<Item> items = new ArrayList<>();
        try {
        	//Observer Pattern
            items = dataModel.getAllItems();  // Retrieves all items from the database via DataModel
            JPanel itemsPanel = new JPanel(new MigLayout("wrap 1", "[grow]", "[]"));

            for (Item item : items) {
                String filePath = item.getImagePath();
                ImageIcon imageIcon = new ImageIcon(filePath);
                JLabel imageLabel = new JLabel();

                // Check if the image was loaded successfully
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

    //  method signatures where database access is performed
    private void viewCustomerDetails() throws SQLException {
        contentPanel.removeAll();
        List<Customer> customers = dataModel.getCustomerDetails();
        for (Customer customer : customers) {
            JPanel customerPanel = new JPanel(new MigLayout("wrap 3", "[grow]10[grow]10[grow]", "[]"));
            String customerDetails = String.format(
                "<html>Username: %s<br>Shipping Address: %s<br>Payment Method: %s</html>",
                customer.getUsername(), customer.getShippingAddress(), customer.getPaymentMethod()
            );
            customerPanel.add(new JLabel(customerDetails));
            JButton viewOrdersButton = new JButton("View Orders");
            viewOrdersButton.addActionListener(e -> {
                try {
                    viewCustomerOrders(customer.getId());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading orders.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            customerPanel.add(viewOrdersButton, "growx");
            JButton viewReviewsButton = new JButton("View Reviews");
            viewReviewsButton.addActionListener(e -> {
                try {
                    viewCustomerReviews(customer.getId());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading reviews.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            customerPanel.add(viewReviewsButton, "growx");
            contentPanel.add(customerPanel);
        }
        refreshContentPanel();
    }

    private void viewCustomerOrders(int customerId) throws SQLException {
        contentPanel.removeAll();
        List<Order> orders = dataModel.getCustomerOrders(customerId);
        if (orders.isEmpty()) {
            contentPanel.add(new JLabel("No orders found for this customer."));
        } else {
            for (Order order : orders) {
                String orderDetails = String.format(
                    "<html>Order ID: %d<br>Total Price: $%.2f<br>Order Date: %s</html>",
                    order.getId(), order.getTotalPrice(), order.getDate().toString()
                );
                contentPanel.add(new JLabel(orderDetails), "wrap");
            }
        }
        refreshContentPanel();
    }

    private void viewCustomerReviews(int customerId) throws SQLException {
        contentPanel.removeAll();
        List<Review> reviews = dataModel.getCustomerReviews(customerId);
        if (reviews.isEmpty()) {
            contentPanel.add(new JLabel("No reviews found for this customer."));
        } else {
            for (Review review : reviews) {
                String reviewDetails = String.format(
                    "<html>Item: %s<br>Rating: %d<br>Comment: %s</html>",
                    review.getItem().getTitle(), review.getRating(), review.getComment()
                );
                contentPanel.add(new JLabel(reviewDetails), "wrap");
            }
        }
        refreshContentPanel();
    }
    private void viewFilteredStockItems(String query) throws SQLException {
        System.out.println("Filtering stock items with query: " + query);
        contentPanel.removeAll();
        List<Item> items = dataModel.searchItems(query);
        System.out.println("Fetched filtered items: " + items.size());

        JPanel itemsPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));

        for (Item item : items) {
            String itemDetails = String.format("<html>Title: %s<br>Manufacturer: %s<br>Price: $%.2f<br>Category: %s<br>Quantity: %d</html>",
                item.getTitle(), item.getManufacturer(), item.getPrice(), item.getCategory(), item.getQuantity());
            JLabel itemLabel = new JLabel(itemDetails);

            JTextField quantityField = new JTextField("0", 5);
            JButton updateButton = new JButton("Update To");
            updateButton.addActionListener(e -> {
                try {
                    int newQuantity = Integer.parseInt(quantityField.getText());
                    if (newQuantity <= 0) {
                        JOptionPane.showMessageDialog(null, "Quantity must be a positive number greater than zero.");
                        return;
                    }
                    dataModel.updateItemStock(item.getId(), newQuantity);
                    JOptionPane.showMessageDialog(null, "Stock updated successfully!");
                    viewFilteredStockItems(query); // Refresh the view
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error updating stock: " + ex.getMessage());
                }
            });

            JButton deleteButton = new JButton("Delete Item");
            deleteButton.addActionListener(e -> {
                try {
					dataModel.deleteItem(item.getId());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(null, "Item deleted successfully!");
                try {
                    viewFilteredStockItems(query);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error refreshing items after delete.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
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

    private void refreshContentPanel() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void update(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DataModel model = new DataModel();
                AdminDashboard dashboard = new AdminDashboard(model); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
