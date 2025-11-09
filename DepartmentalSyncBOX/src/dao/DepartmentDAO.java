package dao;

import model.Department;
import java.util.ArrayList;
import java.util.List;

//public class DepartmentDAO {
//    private List<Department> departments = new ArrayList<>();
//
//    public DepartmentDAO() {
//        departments.add(new Department(1, "HR"));
//        departments.add(new Department(2, "Fi"));
//        departments.add(new Department(3, "IT"));
//    }
//
//    public List<Department> getAllDepartments() { return departments; }
//    public void addDepartment(Department d) { departments.add(d); }
//    public void updateDepartment(Department d) {
//        for (int i = 0; i < departments.size(); i++) {
//            if (departments.get(i).getId() == d.getId()) {
//                departments.set(i, d);
//                return;
//            }
//        }
//    }
//    public void deleteDepartment(int id) { departments.removeIf(d -> d.getId() == id); }
//}
