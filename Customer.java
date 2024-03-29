public class Customer {
    private int id;
    private String username;
    private String password;
    private String shippingAddress;
    private String paymentMethod;

    // Constructor
    public Customer(int id, String username, String shippingAddress, String paymentMethod) {
        this.id = id;
        this.username = username;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public static boolean validateCardDetails(String cardNumber, String cvc, String expiryDate) {
        // Validate card number length
        if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d{16}")) {
            System.out.println("Invalid card number. Must be 16 digits.");
            return false;
        }
        
        // Validate CVC length
        if (cvc == null || cvc.length() != 3 || !cvc.matches("\\d{3}")) {
            System.out.println("Invalid CVC. Must be 3 digits.");
            return false;
        }
        
        // Validate expiry date format
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            System.out.println("Invalid expiry date. Must be in MM/YY format.");
            return false;
        }

        // Additional validation can be done here, such as checking if the card is expired
        return true;
    }
    
}
