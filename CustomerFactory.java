
public class CustomerFactory {
    public static Customer createCustomer(int id, String username, String shippingAddress, String paymentMethod) {
        return new Customer(id, username, shippingAddress, paymentMethod);
    }
}