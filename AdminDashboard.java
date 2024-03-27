import javax.swing.*;

import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionEvent;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the dashboard on exit
        setLayout(new MigLayout("fill", "", "[]push[]"));

        contentPanel = new JPanel(new MigLayout());
        add(contentPanel, "grow, push");

        showAdminControls();

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void showAdminControls() {
        contentPanel.removeAll();
        contentPanel.setLayout(new MigLayout("wrap 1", "[center]", "[]15[]"));

        JButton viewStockButton = new JButton("View Stock Items");
        JButton searchAndUpdateButton = new JButton("Search and Update Stock");

        viewStockButton.addActionListener(this::viewStockItems);
        searchAndUpdateButton.addActionListener(this::searchAndUpdateStock);

        contentPanel.add(viewStockButton, "growx");
        contentPanel.add(searchAndUpdateButton, "growx");

        contentPanel.revalidate();
        contentPanel.repaint();
        pack();
    }

    private void viewStockItems(ActionEvent e) {
        // Implement logic to fetch and display all stock items
        JOptionPane.showMessageDialog(this, "View Stock Items functionality to be implemented.");
    }

    private void searchAndUpdateStock(ActionEvent e) {
        // Implement logic to search and update stock items
        JOptionPane.showMessageDialog(this, "Search and Update Stock functionality to be implemented.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
