import java.util.ArrayList;
import java.util.List;

public class Basket {
    private final List<Item> items = new ArrayList<>();

    // Adds an item to the basket
    public void addItem(Item item) {
        items.add(item);
    }

    // Removes an item from the basket
    public void removeItem(Item item) {
        items.remove(item);
    }

    // Returns a list of all items in the basket
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    // Clears all items from the basket
    public void clear() {
        items.clear();
    }

    // Calculates and returns the total price of all items in the basket
    public double getTotalPrice() {
        return items.stream().mapToDouble(Item::getPrice).sum();
    }

}
