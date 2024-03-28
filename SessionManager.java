public class SessionManager {
    private static String currentUsername = null;
    private static boolean isAdmin = false;

    public static void login(String username, boolean isAdmin) {
        currentUsername = username;
        SessionManager.isAdmin = isAdmin;
    }

    public static void logout() {
        currentUsername = null;
        isAdmin = false;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }
}
