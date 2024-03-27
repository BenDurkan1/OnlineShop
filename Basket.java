import java.util.ArrayList;
import java.util.List;

public class Basket {
    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

	public void clear() {
		// TODO Auto-generated method stub
        items.clear();

	}
	 public void removeItem(Item item) {
	        items.remove(item);
	    }

    // Implement more functionality as needed (e.g., remove items, calculate total, etc.)
}