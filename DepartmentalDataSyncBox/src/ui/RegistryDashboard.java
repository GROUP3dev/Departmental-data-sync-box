package ui;

import dao.AuditLogDAO;
import dao.RecordDAO;
import dao.RecordVisibilityDAO;
import dao.MarksDAO;
import dao.DepartmentDAO;
import dao.UserDAO;
import model.Record;
import model.User;
import model.RecordVisibility;
import model.Marks;
import model.Department;
import util.EncryptionUtil;
import util.NodeConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class RegistryDashboard extends JFrame {
    private final User currentUser;
    private final RecordDAO recordDAO = new RecordDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final RecordVisibilityDAO recordVisibilityDAO = new RecordVisibilityDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    private DefaultTableModel marksTableModel;
    private DefaultTableModel recordsTableModel;

    public RegistryDashboard(User user) {
        this.currentUser = user;
        setTitle("Registry Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Welcome, " + currentUser.getFirstName() + " (REGISTRY)"), BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        header.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Student Records", createRecordsPanel());
        tabbedPane.addTab("Marks Management", createMarksPanel());
        tabbedPane.addTab("Manage Students", createStudentsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "ID", "Student ID", "Title", "Status", "Created At" };
        recordsTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(recordsTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshRecordsTable();

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshRecordsTable());
        timer.start();

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Record");
        JButton btnEdit = new JButton("Edit Record");
        JButton btnDelete = new JButton("Delete Record");
        JButton btnVisibility = new JButton("Manage Visibility");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showAddRecordDialog(null));
        btnEdit.addActionListener(e -> showEditRecordDialog(table));
        btnDelete.addActionListener(e -> deleteRecord(table));
        btnVisibility.addActionListener(e -> manageRecordVisibility(table));
        btnRefresh.addActionListener(e -> refreshRecordsTable());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnVisibility);
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Mark ID", "Record ID", "Course", "Grade", "Score" };
        marksTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(marksTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Initial Load (Empty or All)
        refreshMarksTable();

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshMarksTable());
        timer.start();

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Mark");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showAddMarkDialog(null));
        btnRefresh.addActionListener(e -> refreshMarksTable());

        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "User ID", "Username", "Name", "Email", "Active" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshStudentTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshStudentTable(model));
        timer.start();

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Student");
        JButton btnEdit = new JButton("Update Student");
        JButton btnViewMarks = new JButton("View Marks");
        JButton btnAddMark = new JButton("Add Mark");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showAddStudentDialog(model));
        btnEdit.addActionListener(e -> showEditStudentDialog(table, model));
        btnViewMarks.addActionListener(e -> showStudentMarksDialog(table, model));
        btnAddMark.addActionListener(e -> showAddMarkForStudent(table, model));
        btnRefresh.addActionListener(e -> refreshStudentTable(model));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnViewMarks);
        btnPanel.add(btnAddMark);
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMarksTable() {
        if (marksTableModel == null)
            return;
        marksTableModel.setRowCount(0);
        try {
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                List<Marks> marks = marksDAO.findByRecordId(r.getRecordId());
                for (Marks m : marks) {
                    marksTableModel
                            .addRow(new Object[] { m.getMarkId(), m.getRecordId(), m.getCourseCode(), m.getGrade(),
                                    m.getScore() });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshStudentTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<User> users = userDAO.findAll();
            for (User u : users) {
                // Filter for Role ID 4 (Student)
                if (u.getRoleId() == 4) {
                    model.addRow(new Object[] { u.getUserId(), u.getUsername(),
                            u.getFirstName() + " " + u.getLastName(), u.getEmail(), u.isActive() });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddMarkDialog(Record preSelectedRecord) {
        JDialog dialog = new JDialog(this, "Add Mark", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JComboBox<String> cbRecord = new JComboBox<>();
        try {
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                // Filter: Only allow adding marks to STUDENT records
                if ("STUDENT".equalsIgnoreCase(r.getRecordType())) {
                    String item = "ID: " + r.getRecordId() + " - " + r.getTitle() + " (" + r.getStudentIdNo()
                            + ") - Dept: " + r.getDepartmentOriginId();
                    cbRecord.addItem(item);

                    // Pre-select if matches
                    if (preSelectedRecord != null && r.getRecordId() == preSelectedRecord.getRecordId()) {
                        cbRecord.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTextField txtCourseCode = new JTextField();
        JTextField txtCourseName = new JTextField();
        JTextField txtGrade = new JTextField();
        JTextField txtScore = new JTextField();
        JTextField txtSemester = new JTextField();

        dialog.add(new JLabel("Select Student:"));
        dialog.add(cbRecord);
        dialog.add(new JLabel("Course Code:"));
        dialog.add(txtCourseCode);
        dialog.add(new JLabel("Course Name:"));
        dialog.add(txtCourseName);
        dialog.add(new JLabel("Grade:"));
        dialog.add(txtGrade);
        dialog.add(new JLabel("Score:"));
        dialog.add(txtScore);
        dialog.add(new JLabel("Semester:"));
        dialog.add(txtSemester);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                String selectedRecord = (String) cbRecord.getSelectedItem();
                if (selectedRecord == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a student record.");
                    return;
                }

                // Extract Record ID from string "ID: 123 - ..."
                int recordId = Integer.parseInt(selectedRecord.split(" - ")[0].replace("ID: ", ""));

                Marks m = new Marks();
                m.setSyncUUID(java.util.UUID.randomUUID().toString());
                m.setRecordId(recordId);
                m.setCourseCode(txtCourseCode.getText());
                m.setCourseName(txtCourseName.getText());
                m.setGrade(txtGrade.getText());
                m.setScore(Double.parseDouble(txtScore.getText()));
                m.setSemester(txtSemester.getText());
                m.setIssuedByUserId(currentUser.getUserId());
                m.setSourceActorId(currentUser.getUsername());
                m.setLastModifiedTs(System.currentTimeMillis());

                if (marksDAO.create(m)) {
                    auditLogDAO.log(currentUser.getUserId(), "CREATE_MARK",
                            "Added mark for Record ID: " + m.getRecordId());
                    JOptionPane.showMessageDialog(dialog, "Mark added!");
                    refreshMarksTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add mark.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void showAddMarkForStudent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to add mark.");
            return;
        }
        String username = (String) model.getValueAt(row, 1); // Student Username

        try {
            List<Record> records = recordDAO.findAll();
            Record targetRecord = null;
            int count = 0;

            for (Record r : records) {
                if (r.getStudentIdNo().equalsIgnoreCase(username) && "STUDENT".equalsIgnoreCase(r.getRecordType())) {
                    targetRecord = r;
                    count++;
                }
            }

            if (count == 0) {
                int response = JOptionPane.showConfirmDialog(this,
                        "No academic record found for student: " + username
                                + "\nDo you want to create a Student Record now?",
                        "Record Missing", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    showAddRecordDialog(username);
                }
            } else {
                showAddMarkDialog(targetRecord);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error finding student record: " + e.getMessage());
        }
    }

    private void showAddStudentDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add Student User", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFirstName = new JTextField();
        JTextField txtLastName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDeptId = new JTextField(String.valueOf(currentUser.getDepartmentId())); // Default to Registry's
                                                                                              // Dept

        dialog.add(new JLabel("Username:"));
        dialog.add(txtUsername);
        dialog.add(new JLabel("Password:"));
        dialog.add(txtPassword);
        dialog.add(new JLabel("First Name:"));
        dialog.add(txtFirstName);
        dialog.add(new JLabel("Last Name:"));
        dialog.add(txtLastName);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel("Department ID:"));
        dialog.add(txtDeptId);

        JButton btnSave = new JButton("Create Student");
        btnSave.addActionListener(e -> {
            try {
                User u = new User();
                u.setUsername(txtUsername.getText());
                u.setPasswordHash(EncryptionUtil.hashPassword(new String(txtPassword.getPassword())));
                u.setFirstName(txtFirstName.getText());
                u.setLastName(txtLastName.getText());
                u.setEmail(txtEmail.getText());
                u.setRoleId(4); // Fixed Role ID for STUDENT
                u.setDepartmentId(Integer.parseInt(txtDeptId.getText()));
                u.setActive(true);
                u.setSyncUUID(UUID.randomUUID().toString());
                u.setLastModifiedTs(System.currentTimeMillis());
                u.setSourceActorId(currentUser.getUsername());

                if (userDAO.create(u)) {
                    auditLogDAO.log(currentUser.getUserId(), "CREATE_USER", "Created student user: " + u.getUsername());
                    JOptionPane.showMessageDialog(dialog, "Student user created!");
                    refreshStudentTable(model);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create user (Username might be taken).");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void showEditStudentDialog(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to update.");
            return;
        }
        String username = (String) model.getValueAt(row, 1);
        try {
            List<User> users = userDAO.findAll();
            User targetUser = null;
            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    targetUser = u;
                    break;
                }
            }

            if (targetUser != null) {
                JDialog dialog = new JDialog(this, "Update Student", true);
                dialog.setSize(400, 350);
                dialog.setLayout(new GridLayout(5, 2, 10, 10));
                dialog.setLocationRelativeTo(this);

                JTextField txtFirstName = new JTextField(targetUser.getFirstName());
                JTextField txtLastName = new JTextField(targetUser.getLastName());
                JTextField txtEmail = new JTextField(targetUser.getEmail());
                JCheckBox chkActive = new JCheckBox("Active", targetUser.isActive());

                dialog.add(new JLabel("First Name:"));
                dialog.add(txtFirstName);
                dialog.add(new JLabel("Last Name:"));
                dialog.add(txtLastName);
                dialog.add(new JLabel("Email:"));
                dialog.add(txtEmail);
                dialog.add(new JLabel("Status:"));
                dialog.add(chkActive);

                User finalTargetUser = targetUser;
                JButton btnUpdate = new JButton("Update");
                btnUpdate.addActionListener(e -> {
                    try {
                        finalTargetUser.setFirstName(txtFirstName.getText());
                        finalTargetUser.setLastName(txtLastName.getText());
                        finalTargetUser.setEmail(txtEmail.getText());
                        finalTargetUser.setActive(chkActive.isSelected());
                        finalTargetUser.setLastModifiedTs(System.currentTimeMillis());

                        if (userDAO.update(finalTargetUser)) {
                            auditLogDAO.log(currentUser.getUserId(), "UPDATE_USER",
                                    "Updated student: " + finalTargetUser.getUsername());
                            JOptionPane.showMessageDialog(dialog, "Student updated!");
                            refreshStudentTable(model);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Failed to update student.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel(""));
                dialog.add(btnUpdate);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showStudentMarksDialog(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to view marks.");
            return;
        }
        String username = (String) model.getValueAt(row, 1); // Student Username
        String studentName = (String) model.getValueAt(row, 2);

        JDialog dialog = new JDialog(this, "Marks for " + studentName, true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        String[] columns = { "Course", "Grade", "Score", "Semester", "Assigned By" };
        DefaultTableModel marksModel = new DefaultTableModel(columns, 0);
        JTable marksTable = new JTable(marksModel);
        dialog.add(new JScrollPane(marksTable), BorderLayout.CENTER);

        try {
            // Find all records for this student
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                if (r.getStudentIdNo().equalsIgnoreCase(username)) {
                    List<Marks> marks = marksDAO.findByRecordId(r.getRecordId());
                    for (Marks m : marks) {
                        marksModel.addRow(new Object[] {
                                m.getCourseCode() + " - " + m.getCourseName(),
                                m.getGrade(),
                                m.getScore(),
                                m.getSemester(),
                                m.getSourceActorId()
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        dialog.add(btnClose, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void refreshRecordsTable() {
        if (recordsTableModel == null)
            return;
        recordsTableModel.setRowCount(0);
        try {
            List<Record> records = recordDAO.findAll();
            for (Record r : records) {
                recordsTableModel
                        .addRow(new Object[] { r.getRecordId(), r.getStudentIdNo(), r.getTitle(), r.getStatus(),
                                r.getCreatedAt() });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddRecordDialog(String preFilledStudentId) {
        JDialog dialog = new JDialog(this, "Add New Record", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtStudentId = new JTextField(preFilledStudentId != null ? preFilledStudentId : "");
        JTextField txtTitle = new JTextField();
        JTextArea txtDesc = new JTextArea();
        JComboBox<String> cbType = new JComboBox<>(new String[] { "STUDENT", "STAFF" });

        dialog.add(new JLabel("Student ID No:"));
        dialog.add(txtStudentId);
        dialog.add(new JLabel("Title:"));
        dialog.add(txtTitle);
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(txtDesc));
        dialog.add(new JLabel("Type:"));
        dialog.add(cbType);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                Record r = new Record();
                r.setStudentIdNo(txtStudentId.getText());
                r.setTitle(txtTitle.getText());
                r.setDescription(txtDesc.getText());
                r.setRecordType(cbType.getSelectedItem().toString());
                r.setDepartmentOriginId(currentUser.getDepartmentId());
                r.setCreatedBy(currentUser.getUserId());
                r.setStatus("PENDING");
                r.setSourceActorId(currentUser.getUsername()); // Or UUID
                r.setSyncUUID(java.util.UUID.randomUUID().toString());
                r.setLastModifiedTs(System.currentTimeMillis());

                if (recordDAO.create(r)) {
                    auditLogDAO.log(currentUser.getUserId(), "CREATE_RECORD", "Created record: " + r.getTitle());
                    JOptionPane.showMessageDialog(dialog, "Record created!");
                    refreshRecordsTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create record.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void showEditRecordDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to edit.");
            return;
        }
        int id = (int) recordsTableModel.getValueAt(row, 0);
        try {
            Record r = recordDAO.findById(id);
            if (r != null) {
                JDialog dialog = new JDialog(this, "Edit Record", true);
                dialog.setSize(400, 400);
                dialog.setLayout(new GridLayout(6, 2, 10, 10));
                dialog.setLocationRelativeTo(this);

                JTextField txtStudentId = new JTextField(r.getStudentIdNo());
                JTextField txtTitle = new JTextField(r.getTitle());
                JTextArea txtDesc = new JTextArea(r.getDescription());
                JComboBox<String> cbType = new JComboBox<>(new String[] { "STUDENT", "STAFF" });
                cbType.setSelectedItem(r.getRecordType());
                JComboBox<String> cbStatus = new JComboBox<>(
                        new String[] { "DRAFT", "PENDING", "APPROVED", "ARCHIVED" });
                cbStatus.setSelectedItem(r.getStatus());

                dialog.add(new JLabel("Student ID No:"));
                dialog.add(txtStudentId);
                dialog.add(new JLabel("Title:"));
                dialog.add(txtTitle);
                dialog.add(new JLabel("Description:"));
                dialog.add(new JScrollPane(txtDesc));
                dialog.add(new JLabel("Type:"));
                dialog.add(cbType);
                dialog.add(new JLabel("Status:"));
                dialog.add(cbStatus);

                JButton btnSave = new JButton("Update");
                btnSave.addActionListener(e -> {
                    try {
                        r.setStudentIdNo(txtStudentId.getText());
                        r.setTitle(txtTitle.getText());
                        r.setDescription(txtDesc.getText());
                        r.setRecordType(cbType.getSelectedItem().toString());
                        r.setStatus(cbStatus.getSelectedItem().toString());
                        r.setLastModifiedTs(System.currentTimeMillis());

                        if (recordDAO.update(r)) {
                            auditLogDAO.log(currentUser.getUserId(), "UPDATE_RECORD",
                                    "Updated record: " + r.getTitle());
                            JOptionPane.showMessageDialog(dialog, "Record updated!");
                            refreshRecordsTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Failed to update record.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    }
                });

                dialog.add(new JLabel(""));
                dialog.add(btnSave);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteRecord(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.");
            return;
        }
        int id = (int) recordsTableModel.getValueAt(row, 0);
        try {
            Record r = recordDAO.findById(id);
            if (r != null
                    && JOptionPane.showConfirmDialog(this, "Delete " + r.getTitle() + "?") == JOptionPane.YES_OPTION) {
                if (recordDAO.softDelete(r.getSyncUUID())) {
                    auditLogDAO.log(currentUser.getUserId(), "DELETE_RECORD", "Deleted record: " + r.getTitle());
                    refreshRecordsTable();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void manageRecordVisibility(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to manage visibility.");
            return;
        }
        int recordId = (int) recordsTableModel.getValueAt(row, 0);

        JComboBox<String> cbDept = new JComboBox<>();
        try {
            List<Department> depts = departmentDAO.findAll();
            for (Department d : depts) {
                cbDept.addItem(d.getDepartmentId() + " - " + d.getDepartmentName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading departments: " + e.getMessage());
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, cbDept, "Select Department to Grant Visibility",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) cbDept.getSelectedItem();
            if (selected != null) {
                try {
                    int deptId = Integer.parseInt(selected.split(" - ")[0]);
                    RecordVisibility rv = new RecordVisibility();
                    rv.setRecordId(recordId);
                    rv.setDepartmentId(deptId);
                    rv.setGrantedByUserId(currentUser.getUserId());
                    rv.setGrantedAt(new java.sql.Timestamp(System.currentTimeMillis()));

                    if (recordVisibilityDAO.grantVisibility(rv)) {
                        auditLogDAO.log(currentUser.getUserId(), "GRANT_VISIBILITY",
                                "Granted visibility of Record " + recordId + " to Dept " + deptId);
                        JOptionPane.showMessageDialog(this, "Visibility granted!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to grant visibility (might already exist).");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid Department ID.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }
    }
}
