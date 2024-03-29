import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private DBHelper dbHelper = new DBHelper(); // Assuming default constructor is available

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("fill", "", "[]push[]"));

        contentPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
        add(contentPanel, "grow, push");

        // Call viewStockItems directly to display items on dashboard open
        viewStockItems();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void viewStockItems() {
        contentPanel.removeAll();
        try {
            List<Item> items = dbHelper.fetchAllItems();
            for (Item item : items) {
                // Display all details for each item
                String itemDetails = String.format("<html>Title: %s<br>Manufacturer: %s<br>Price: $%.2f<br>Category: %s<br>Quantity: %d</html>",
                        item.getTitle(), item.getManufacturer(), item.getPrice(), item.getCategory(), item.getQuantity());
                contentPanel.add(new JLabel(itemDetails));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching stock items: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Optionally add a refresh button to allow admin to refresh the item list manually
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> viewStockItems());
        contentPanel.add(refreshButton, "span, growx");

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
 // Assuming DBHelper has methods like `searchItems(String query)` and `updateItemStock(int itemId, int newQuantity)`

    private void setupSearchAndUpdateUI() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 2", "[grow]10[grow]", "[]10[]"));
        
        // Search field and button
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> viewFilteredStockItems(searchField.getText()));
        contentPanel.add(searchField, "grow");
        contentPanel.add(searchButton, "wrap");
        
        viewFilteredStockItems(""); // Initially display all items
    }

    private void viewFilteredStockItems(String query) {
        JPanel itemsPanel = new JPanel(new MigLayout("wrap 4", "[grow]10[grow]10[grow]10[grow]", "[]"));
        
        try {
            List<Item> items = query.isEmpty() ? dbHelper.fetchAllItems() : dbHelper.searchItems(query);
            
            for (Item item : items) {
                String itemDetails = String.format("<html>Title: %s<br>Manufacturer: %s<br>Price: $%.2f<br>Category: %s<br>Quantity: </html>",
                    item.getTitle(), item.getManufacturer(), item.getPrice(), item.getCategory());
                JLabel itemLabel = new JLabel(itemDetails);
                
                JTextField quantityField = new JTextField(String.valueOf(item.getQuantity()), 5);
                JButton updateButton = new JButton("Update");
                updateButton.addActionListener(e -> {
                    try {
                        int newQuantity = Integer.parseInt(quantityField.getText());
                        dbHelper.updateItemStock(item.getId(), newQuantity);
                        JOptionPane.showMessageDialog(null, "Stock updated successfully!");
                        viewFilteredStockItems(query); // Refresh the view
                    } catch (NumberFormatException | SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating stock: " + ex.getMessage());
                    }
                });
                
                itemsPanel.add(itemLabel);
                itemsPanel.add(quantityField);
                itemsPanel.add(updateButton);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching items: " + ex.getMessage());
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        contentPanel.add(scrollPane, "push, grow, span");
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
