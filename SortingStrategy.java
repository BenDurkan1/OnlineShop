import java.util.Collections;
import java.util.Comparator;
import java.util.List;

interface SortingStrategy {
    void sort(List<ItemInt> items);
}

class SortByTitle implements SortingStrategy {
    @Override
    public void sort(List<ItemInt> items) {
        Collections.sort(items, Comparator.comparing(ItemInt::getTitle));
    }
}

class SortByPrice implements SortingStrategy {
    @Override
    public void sort(List<ItemInt> items) {
        Collections.sort(items, Comparator.comparingDouble(ItemInt::getPrice));
    }
}

class TitleSortingStrategy implements SortingStrategy {
 @Override
 public void sort(List<ItemInt> items) {
     Collections.sort(items, Comparator.comparing(ItemInt::getTitle));
 }
}

class PriceSortingStrategy implements SortingStrategy {
 @Override
 public void sort(List<ItemInt> items) {
     Collections.sort(items, Comparator.comparingDouble(ItemInt::getPrice));
 }
}


class ManufacturerSortingStrategy implements SortingStrategy {
 @Override
 public void sort(List<ItemInt> items) {
     Collections.sort(items, Comparator.comparing(ItemInt::getManufacturer));
 }
}