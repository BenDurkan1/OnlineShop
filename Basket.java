import java.util.ArrayList;
import java.util.List;

public class Basket {
    private final List<ItemInt> items = new ArrayList<>();

    public void addItem(ItemInt decoratedItem) {
        items.add(decoratedItem);
    }

    public void removeItem(ItemInt item) {
        items.remove(item);
    }

    public List<ItemInt> getItems() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(ItemInt::getPrice).sum();
    }
}
