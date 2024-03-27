public class SessionManager {
    private static Customer currentCustomer = null;

    public static void login(Customer customer) {
        currentCustomer = customer;
    }

    public static void logout() {
        currentCustomer = null;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static boolean isLoggedIn() {
        return currentCustomer != null;
    }
}
