package ui;

import dao.RecordDAO;
import dao.MarksDAO;
import model.Record;
import model.User;
import model.Marks;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentDashboard extends JFrame {
    private final User currentUser;
    private final RecordDAO recordDAO = new RecordDAO();
    private final MarksDAO marksDAO = new MarksDAO();

    public StudentDashboard(User user) {
        this.currentUser = user;
        setTitle("Student Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("My Academic Records (" + currentUser.getFirstName() + ")"), BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        header.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Records", createRecordsPanel());
        tabbedPane.addTab("My Marks", createMarksPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = { "Title", "Description", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load Data
        refreshRecordsTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshRecordsTable(model));
        timer.start();

        // Refresh Button
        JPanel btnPanel = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshRecordsTable(model));
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Course", "Grade", "Score", "Semester", "Assigned By" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshMarksTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshMarksTable(model));
        timer.start();

        JPanel btnPanel = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshMarksTable(model));
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRecordsTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                // Simple filter: if record student ID matches username
                if (r.getStudentIdNo().equalsIgnoreCase(currentUser.getUsername())) {
                    model.addRow(new Object[] { r.getTitle(), r.getDescription(), r.getStatus() });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshMarksTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                if (r.getStudentIdNo().equalsIgnoreCase(currentUser.getUsername())) {
                    List<Marks> marks = marksDAO.findByRecordId(r.getRecordId());
                    for (Marks m : marks) {
                        model.addRow(new Object[] { m.getCourseCode() + " - " + m.getCourseName(), m.getGrade(),
                                m.getScore(), m.getSemester(), m.getSourceActorId() });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}