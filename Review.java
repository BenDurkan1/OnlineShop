public class Review {
    private int id;
    private Item item;
    private Customer customer;
    private int rating;
    private String comment;

    // Constructor
    public Review(int id, Item item, Customer customer, int rating, String comment) {
        this.id = id;
        this.item = item;
        this.customer = customer;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}