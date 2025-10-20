package dao;

import model.User;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private List<User> users = new ArrayList<>();

    public UserDAO() {
        users.add(new User(1, "admin", "1234", "admin"));
        users.add(new User(2, "staff1", "1234", "staff"));
        users.add(new User(3, "user1", "1234", "user"));
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() { return users; }
    public void addUser(User u) { users.add(u); }
    public void updateUser(User u) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == u.getId()) {
                users.set(i, u);
                return;
            }
        }
    }
    public void deleteUser(int id) { users.removeIf(u -> u.getId() == id); }
}
