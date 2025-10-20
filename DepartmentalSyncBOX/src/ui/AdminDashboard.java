package ui;


import dao.DepartmentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.User;

public class AdminDashboard extends JFrame {

    private DepartmentDAO dao = new DepartmentDAO();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtId = new JTextField(5);
    private JTextField txtName = new JTextField(10);
    private JTextField txtLocation = new JTextField(10);

    public AdminDashboard(User user) {
        setTitle("Departmental Data Sync Box - Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== MENU BAR =====
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuAdmin = new JMenu("Admin");
        JMenu menuDept = new JMenu("Department");
        JMenu menuReport = new JMenu("Reports");
        menuBar.add(menuFile);
        menuBar.add(menuAdmin);
        menuBar.add(menuDept);
        menuBar.add(menuReport);
        setJMenuBar(menuBar);

        // ===== TABLE =====
        String[] columns = {"ID", "Name", "Location"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        refreshTable();

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel panel = new JPanel();
        panel.add(new JLabel("ID:"));
        panel.add(txtId);
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Location:"));
        panel.add(txtLocation);

        JButton btnAdd = new JButton("Add");
        panel.add(btnAdd);

        JButton btnRefresh = new JButton("Refresh");
        panel.add(btnRefresh);

        add(panel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> {
            int id = Integer.parseInt(txtId.getText());
            String name = txtName.getText();
            String location = txtLocation.getText();
            dao.addDepartment(new Department(id, name, location));
            refreshTable();
            JOptionPane.showMessageDialog(this, "Department Added Successfully!");
        });

        btnRefresh.addActionListener(e -> refreshTable());

        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Welcome Admin: " + user.getUsername(), SwingConstants.CENTER));



    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Department> list = dao.getAllDepartments();
        for (Department d : list) {
            model.addRow(new Object[]{d.getId(), d.getName(), d.getLocation()});
        }
    }

}
