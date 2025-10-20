package dao;

import model.Staff;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private List<Staff> staffList = new ArrayList<>();

    public StaffDAO() {
        staffList.add(new Staff(1, "Alice", "IT", "alice@company.com"));
        staffList.add(new Staff(2, "Bob", "HR", "bob@company.com"));
    }

    public List<Staff> getAllStaff() {
        return staffList;
    }

    public void addStaff(Staff s) {
        staffList.add(s);
    }

    public void removeStaff(int id) {
        staffList.removeIf(st -> st.getId() == id);
    }
}
