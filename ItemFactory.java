

public class ItemFactory {
    public static Item createItem(int id, String title, String manufacturer, double price, String category, int quantity, String imagePath) {
        return new Item(id, title, manufacturer, price, category, quantity, imagePath);
    }
}

