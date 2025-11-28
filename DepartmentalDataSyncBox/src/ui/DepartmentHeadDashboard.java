package ui;

import dao.AuditLogDAO;
import dao.RecordDAO;
import model.Record;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DepartmentHeadDashboard extends JFrame {
    private final User currentUser;
    private final RecordDAO recordDAO = new RecordDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public DepartmentHeadDashboard(User user) {
        this.currentUser = user;
        setTitle("Dept Head Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Department ID: " + currentUser.getDepartmentId()), BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        header.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Student ID", "Title", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load Data
        refreshTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshTable(model));
        timer.start();

        // Controls
        JPanel controls = new JPanel();
        JButton btnApprove = new JButton("Approve Selected");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExport = new JButton("Export Report");

        btnApprove.addActionListener(e -> approveSelected(table, model));
        btnRefresh.addActionListener(e -> refreshTable(model));
        btnExport.addActionListener(e -> exportReport());

        controls.add(btnApprove);
        controls.add(btnRefresh);
        controls.add(btnExport);
        controls.add(new JLabel("(Auto-refreshes every 5s)"));
        mainPanel.add(controls, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Record> records = recordDAO.findByDepartment(currentUser.getDepartmentId());
            for (Record r : records) {
                model.addRow(new Object[] {
                        r.getRecordId(), r.getStudentIdNo(), r.getTitle(), r.getStatus()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void approveSelected(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to approve.");
            return;
        }

        int recordId = (int) model.getValueAt(row, 0);

        try {
            List<Record> all = recordDAO.findAll();
            Record target = all.stream().filter(r -> r.getRecordId() == recordId).findFirst().orElse(null);

            if (target != null) {
                target.setStatus("APPROVED");
                if (recordDAO.update(target)) {
                    // Audit Log: APPROVE_RECORD
                    auditLogDAO.log(currentUser.getUserId(), "APPROVE_RECORD",
                            "Approved Record ID: " + target.getRecordId());
                    JOptionPane.showMessageDialog(this, "Record Approved!");
                    refreshTable(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
            }
            try {
                List<Record> records = recordDAO.findByDepartment(currentUser.getDepartmentId());
                util.ReportUtil.generateCSV(records, path);
                JOptionPane.showMessageDialog(this, "Report saved successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage());
            }
        }
    }
}