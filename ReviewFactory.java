public class ReviewFactory {
    public static Review createReview(int id, ItemInt item, Customer customer, int rating, String comment) {
        return new Review(id, item, customer, rating, comment);
    }
}