public class Item {
    private int id;
    private String title;
    private String manufacturer;
    private double price;
    private String category;
    private int quantity; // New field for quantity

    // Constructor
    public Item(int id, String title, String manufacturer, double price, String category, int quantity) {
        this.id = id;
        this.title = title;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    

	// Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }






	
}
