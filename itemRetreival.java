import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class itemRetreival {

    public List<Item> fetchItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("manufacturer"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getInt("quantity")); 
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
