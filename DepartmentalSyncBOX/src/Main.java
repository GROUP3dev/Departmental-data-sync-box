import model.User;
import ui.AdminDashboard;

public class Main {
    public static void main(String[] args) {
        User admin = new User(1, "admin", "1234", "admin");
        new AdminDashboard(admin).setVisible(true);


    }
}
