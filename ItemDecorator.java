public abstract class ItemDecorator implements ItemInt {
    protected Item decoratedItem;

    public ItemDecorator(Item item) {
        this.decoratedItem = item;
    }

    @Override
    public double getPrice() {
        return decoratedItem.getPrice();
    }

    @Override
    public String getDescription() {
        return decoratedItem.getDescription();
    }

    @Override
    public String getTitle() {
        return decoratedItem.getTitle();
    }

    @Override
    public String getManufacturer() {
        return decoratedItem.getManufacturer();
    }

    @Override
    public int getId() {
        return decoratedItem.getId();
    }

    @Override
    public String getCategory() {
        return decoratedItem.getCategory();
    }

    @Override
    public int getQuantity() {
        return decoratedItem.getQuantity();
    }

    @Override
    public String getImagePath() {
        return decoratedItem.getImagePath();
    }
    public Item getDecoratedItem() {
        return decoratedItem;
    }
}

//DiscountDecorator class that modifies the price and description
class DiscountDecorator extends ItemDecorator {
    private double discount;

    public DiscountDecorator(Item item, double discountPercentage) {
        super(item);
        this.discount = discountPercentage;
    }

    @Override
    public double getPrice() {
        return super.decoratedItem.getPrice() * (1 - discount / 100);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " (Discounted by " + discount + "%)";
    }
    @Override
    public String getImagePath() {
        return decoratedItem.getImagePath();
    }
    
    @Override
    public int getId() {
        return decoratedItem.getId();
    }
    
    @Override
    public int getQuantity() {
        return decoratedItem.getQuantity();
    }
   
}

