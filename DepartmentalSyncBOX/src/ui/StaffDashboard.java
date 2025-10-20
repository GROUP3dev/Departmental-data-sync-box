package ui;

import model.User;
import dao.DepartmentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffDashboard extends JFrame {

    private User loggedInUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private DepartmentDAO departmentDAO = new DepartmentDAO(); // mock data

    public StaffDashboard(User user) {
        this.loggedInUser = user;

        setTitle("Staff Dashboard - " + loggedInUser.getUsername());
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome Staff: " + loggedInUser.getUsername(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // Table for departments
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
        table = new JTable(tableModel);
        refreshTable();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department d : departments) {
            tableModel.addRow(new Object[]{d.getId(), d.getName()});
        }
    }
}
