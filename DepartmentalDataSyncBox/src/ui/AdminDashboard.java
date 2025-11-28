package ui;

import dao.AuditLogDAO;
import dao.DepartmentDAO;
import dao.RecordDAO;
import dao.RecordVisibilityDAO;
import dao.RoleDAO;
import dao.SyncQueueDAO;
import dao.UserDAO;
import model.AuditLog;
import model.Department;
import model.Record;
import model.RecordVisibility;
import model.Role;
import model.User;
import sync.SyncManager;
import ui.dialogs.ConflictResolverDialog;
import util.EncryptionUtil;
import util.ReportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final SyncQueueDAO syncQueueDAO = new SyncQueueDAO();
    private final RecordVisibilityDAO recordVisibilityDAO = new RecordVisibilityDAO();
    private final RecordDAO recordDAO = new RecordDAO();

    public AdminDashboard(User user) {
        this.currentUser = user;
        setTitle("Admin Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Departments", createDepartmentPanel());
        tabbedPane.addTab("Shared Data", createSharedDataPanel());
        tabbedPane.addTab("Sync Monitoring", createSyncMonitoringPanel());
        tabbedPane.addTab("Active Sessions", createActiveSessionsPanel());
        tabbedPane.addTab("Cloud Backup", createBackupRestorePanel());
        tabbedPane.addTab("Audit Logs", createAuditLogPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Logged in as: " + currentUser.getFirstName() + " (ADMIN)"), BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private JPanel createSharedDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Record ID", "Record Title", "Shared With Dept", "Granted By", "Granted At" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshSharedDataTable(model));

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshSharedDataTable(model));
        timer.start();

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        refreshSharedDataTable(model);

        return panel;
    }

    private void refreshSharedDataTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<RecordVisibility> visibilities = recordVisibilityDAO.getAllVisibilities();
            for (RecordVisibility rv : visibilities) {
                String recordTitle = "Unknown";
                String deptName = "Unknown";

                Record r = recordDAO.findById(rv.getRecordId());
                if (r != null) {
                    recordTitle = r.getTitle();
                }

                Department d = departmentDAO.findById(rv.getDepartmentId());
                if (d != null) {
                    deptName = d.getDepartmentName();
                }

                model.addRow(new Object[] {
                        rv.getRecordId(),
                        recordTitle,
                        deptName + " (ID: " + rv.getDepartmentId() + ")",
                        rv.getGrantedByUserId(),
                        rv.getGrantedAt()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = { "ID", "Username", "Role", "Dept ID", "Email", "Active" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load Data
        refreshUserTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshUserTable(model));
        timer.start();

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showAddUserDialog(model));
        btnEdit.addActionListener(e -> showEditUserDialog(table, model));
        btnRefresh.addActionListener(e -> refreshUserTable(model));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshUserTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            // Fetch all roles for mapping
            List<Role> roles = roleDAO.findAll();
            Map<Integer, String> roleMap = new HashMap<>();
            for (Role r : roles) {
                roleMap.put(r.getRoleId(), r.getRoleName());
            }

            List<User> users = userDAO.findAll();
            for (User u : users) {
                String roleDisplay = u.getRoleId() + " - " + roleMap.getOrDefault(u.getRoleId(), "Unknown");
                model.addRow(new Object[] { u.getUserId(), u.getUsername(), roleDisplay, u.getDepartmentId(),
                        u.getEmail(), u.isActive() });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddUserDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        JComboBox<String> cbRole = new JComboBox<>();
        try {
            List<Role> roles = roleDAO.findAll();
            for (Role r : roles) {
                cbRole.addItem(r.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTextField txtDept = new JTextField("1"); // Default Dept ID
        JTextField txtFirst = new JTextField();
        JTextField txtLast = new JTextField();
        JTextField txtEmail = new JTextField();

        dialog.add(new JLabel("Username:"));
        dialog.add(txtUser);
        dialog.add(new JLabel("Password:"));
        dialog.add(txtPass);
        dialog.add(new JLabel("Role:"));
        dialog.add(cbRole);
        dialog.add(new JLabel("Department ID:"));
        dialog.add(txtDept);
        dialog.add(new JLabel("First Name:"));
        dialog.add(txtFirst);
        dialog.add(new JLabel("Last Name:"));
        dialog.add(txtLast);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                User u = new User();
                u.setUsername(txtUser.getText());
                u.setPasswordHash(EncryptionUtil.hashPassword(new String(txtPass.getPassword())));

                String selectedRole = (String) cbRole.getSelectedItem();
                if (selectedRole != null) {
                    u.setRoleId(Integer.parseInt(selectedRole.split(" ")[0]));
                }

                u.setDepartmentId(Integer.parseInt(txtDept.getText()));
                u.setFirstName(txtFirst.getText());
                u.setLastName(txtLast.getText());
                u.setEmail(txtEmail.getText());
                u.setActive(true);

                if (userDAO.create(u)) {
                    // Audit Log: CREATE_USER
                    auditLogDAO.log(currentUser.getUserId(), "CREATE_USER",
                            "Created user: " + u.getUsername() + " (Role: " + u.getRoleId() + ")");
                    JOptionPane.showMessageDialog(dialog, "User created successfully!");
                    refreshUserTable(model);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create user.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void showEditUserDialog(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        User userToEdit = null;
        try {
            List<User> users = userDAO.findAll();
            for (User u : users) {
                if (u.getUserId() == userId) {
                    userToEdit = u;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (userToEdit == null)
            return;

        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtUser = new JTextField(userToEdit.getUsername());
        JPasswordField txtPass = new JPasswordField(); // Leave empty to keep existing

        JComboBox<String> cbRole = new JComboBox<>();
        try {
            List<Role> roles = roleDAO.findAll();
            for (Role r : roles) {
                cbRole.addItem(r.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set selected role
        for (int i = 0; i < cbRole.getItemCount(); i++) {
            if (cbRole.getItemAt(i).startsWith(String.valueOf(userToEdit.getRoleId()))) {
                cbRole.setSelectedIndex(i);
                break;
            }
        }

        JTextField txtDept = new JTextField(String.valueOf(userToEdit.getDepartmentId()));
        JTextField txtFirst = new JTextField(userToEdit.getFirstName());
        JTextField txtLast = new JTextField(userToEdit.getLastName());
        JTextField txtEmail = new JTextField(userToEdit.getEmail());
        JCheckBox chkActive = new JCheckBox("Active", userToEdit.isActive());

        dialog.add(new JLabel("Username:"));
        dialog.add(txtUser);
        dialog.add(new JLabel("New Password (blank to keep):"));
        dialog.add(txtPass);
        dialog.add(new JLabel("Role:"));
        dialog.add(cbRole);
        dialog.add(new JLabel("Department ID:"));
        dialog.add(txtDept);
        dialog.add(new JLabel("First Name:"));
        dialog.add(txtFirst);
        dialog.add(new JLabel("Last Name:"));
        dialog.add(txtLast);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel("Status:"));
        dialog.add(chkActive);

        JButton btnSave = new JButton("Update");
        User finalUserToEdit = userToEdit;
        btnSave.addActionListener(e -> {
            try {
                finalUserToEdit.setUsername(txtUser.getText());
                String newPass = new String(txtPass.getPassword());
                if (!newPass.isEmpty()) {
                    finalUserToEdit.setPasswordHash(EncryptionUtil.hashPassword(newPass));
                }

                String selectedRole = (String) cbRole.getSelectedItem();
                if (selectedRole != null) {
                    finalUserToEdit.setRoleId(Integer.parseInt(selectedRole.split(" ")[0]));
                }

                finalUserToEdit.setDepartmentId(Integer.parseInt(txtDept.getText()));
                finalUserToEdit.setFirstName(txtFirst.getText());
                finalUserToEdit.setLastName(txtLast.getText());
                finalUserToEdit.setEmail(txtEmail.getText());
                finalUserToEdit.setActive(chkActive.isSelected());

                if (userDAO.update(finalUserToEdit)) {
                    // Audit Log: UPDATE_USER
                    auditLogDAO.log(currentUser.getUserId(), "UPDATE_USER",
                            "Updated user: " + finalUserToEdit.getUsername());
                    JOptionPane.showMessageDialog(dialog, "User updated successfully!");
                    refreshUserTable(model);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update user.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private JPanel createSyncMonitoringPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Split Pane: Top = Queue, Bottom = Logs
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // --- Top: Sync Queue ---
        JPanel queuePanel = new JPanel(new BorderLayout());
        queuePanel.setBorder(BorderFactory.createTitledBorder("Sync Queue"));
        String[] queueCols = { "Queue ID", "Entity", "Operation", "Status", "Retry Count", "Created At" };
        DefaultTableModel queueModel = new DefaultTableModel(queueCols, 0);
        JTable queueTable = new JTable(queueModel);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);
        splitPane.setTopComponent(queuePanel);

        // --- Bottom: Sync Logs ---
        JPanel logsPanel = new JPanel(new BorderLayout());
        logsPanel.setBorder(BorderFactory.createTitledBorder("Recent Sync Logs"));
        String[] logCols = { "Time", "Remote IP", "Direction", "Status", "Items", "Details" };
        DefaultTableModel logModel = new DefaultTableModel(logCols, 0);
        JTable logTable = new JTable(logModel);
        logsPanel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(logsPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        // --- Controls ---
        JPanel controls = new JPanel();
        JButton btnSyncLan = new JButton("Trigger LAN Sync");
        JButton btnRefresh = new JButton("Refresh All");
        JButton btnExport = new JButton("Export Report");
        JButton btnResolve = new JButton("Manual Resolve");
        JTextField txtIp = new JTextField("127.0.0.1", 15);

        btnSyncLan.addActionListener(e -> {
            // Audit Log: MANUAL_SYNC
            auditLogDAO.log(currentUser.getUserId(), "MANUAL_SYNC", "Triggered LAN sync to " + txtIp.getText());
            SyncManager.getInstance().initiateSync(SyncManager.SyncType.LAN, txtIp.getText());
            JOptionPane.showMessageDialog(this, "Sync Initiated to " + txtIp.getText());
        });

        btnRefresh.addActionListener(e -> refreshSyncMonitoring(queueModel, logModel));

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshSyncMonitoring(queueModel, logModel));
        timer.start();

        btnExport.addActionListener(e -> exportReport());

        btnResolve.addActionListener(e -> {
            new ConflictResolverDialog(this).setVisible(true);
        });

        controls.add(new JLabel("Target IP:"));
        controls.add(txtIp);
        controls.add(btnSyncLan);
        controls.add(btnRefresh);
        controls.add(btnExport);
        controls.add(btnResolve);
        controls.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(controls, BorderLayout.NORTH);

        // Initial load
        refreshSyncMonitoring(queueModel, logModel);

        return panel;
    }

    private void refreshSyncMonitoring(DefaultTableModel queueModel, DefaultTableModel logModel) {
        // Refresh Queue
        queueModel.setRowCount(0);
        List<model.SyncQueue> items = syncQueueDAO.getAllQueueItems(100);
        for (model.SyncQueue item : items) {
            queueModel.addRow(new Object[] {
                    item.getQueueId(), item.getEntityName(), item.getOperationType(),
                    item.getStatus(), item.getRetryCount(), item.getCreatedAt()
            });
        }

        // Refresh Logs
        logModel.setRowCount(0);
        List<model.SyncLog> logs = new dao.SyncLogDAO().getRecentLogs();
        for (model.SyncLog l : logs) {
            logModel.addRow(new Object[] {
                    l.getSyncTimestamp(), l.getRemoteActorId(), l.getDirection(),
                    l.getStatus(), l.getItemsProcessed(), l.getDetails()
            });
        }
    }

    private JPanel createBackupRestorePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.append("Cloud Backup & Restore Console\n");
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Settings Panel
        JPanel settingsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));

        // Row 1: Local Archive Path
        JPanel localPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtLocalPath = new JTextField(30);
        String savedLocal = util.ConfigUtil.getProperty("backup.local.path");
        if (savedLocal != null)
            txtLocalPath.setText(savedLocal);

        JButton btnBrowseLocal = new JButton("Browse Archive Folder");
        btnBrowseLocal.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtLocalPath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        localPanel.add(new JLabel("Local Archive Folder:"));
        localPanel.add(txtLocalPath);
        localPanel.add(btnBrowseLocal);

        // Row 2: Google Drive Path
        JPanel googlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtGooglePath = new JTextField(30);
        String savedGoogle = util.ConfigUtil.getProperty("backup.google.path");
        if (savedGoogle != null)
            txtGooglePath.setText(savedGoogle);

        JButton btnBrowseGoogle = new JButton("Browse Drive Folder");
        btnBrowseGoogle.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtGooglePath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        googlePanel.add(new JLabel("Google Drive Folder:"));
        googlePanel.add(txtGooglePath);
        googlePanel.add(btnBrowseGoogle);

        // Row 3: Online Link
        JPanel onlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtOnlineLink = new JTextField(30);
        String savedLink = util.ConfigUtil.getProperty("backup.online.link");
        if (savedLink != null)
            txtOnlineLink.setText(savedLink);

        JButton btnViewOnline = new JButton("Open in Browser");
        btnViewOnline.addActionListener(e -> {
            String url = txtOnlineLink.getText();
            if (url != null && !url.isEmpty()) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to open link: " + ex.getMessage());
                }
            }
        });
        onlinePanel.add(new JLabel("Online Drive Link:"));
        onlinePanel.add(txtOnlineLink);
        onlinePanel.add(btnViewOnline);

        // Row 4: Save Button
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSaveSettings = new JButton("Save Configuration");
        btnSaveSettings.addActionListener(e -> {
            util.ConfigUtil.setProperty("backup.local.path", txtLocalPath.getText());
            util.ConfigUtil.setProperty("backup.google.path", txtGooglePath.getText());
            util.ConfigUtil.setProperty("backup.online.link", txtOnlineLink.getText());
            JOptionPane.showMessageDialog(this, "Settings saved to db.properties!");
        });
        savePanel.add(btnSaveSettings);

        settingsPanel.add(localPanel);
        settingsPanel.add(googlePanel);
        settingsPanel.add(onlinePanel);
        settingsPanel.add(savePanel);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBackup = new JButton("Backup to Local Drive");
        JButton btnRestore = new JButton("Restore from Backup");

        sync.CloudSync cloudSync = new sync.CloudSync();

        btnBackup.addActionListener(e -> {
            String localDir = util.ConfigUtil.getProperty("backup.local.path");
            String googleDir = util.ConfigUtil.getProperty("backup.google.path");

            if ((localDir == null || localDir.isEmpty()) && (googleDir == null || googleDir.isEmpty())) {
                JOptionPane.showMessageDialog(this,
                        "Please configure at least one backup folder (Local or Google Drive).");
                return;
            }

            new Thread(() -> {
                java.util.concurrent.atomic.AtomicBoolean localSuccess = new java.util.concurrent.atomic.AtomicBoolean(
                        false);
                java.util.concurrent.atomic.AtomicBoolean googleSuccess = new java.util.concurrent.atomic.AtomicBoolean(
                        false);

                // 1. Local Backup
                if (localDir != null && !localDir.isEmpty()) {
                    SwingUtilities.invokeLater(() -> logArea.append("Starting Local Backup to: " + localDir + "\n"));
                    if (cloudSync.backupToDirectory(localDir)) {
                        localSuccess.set(true);
                        SwingUtilities.invokeLater(() -> logArea.append("Local Backup successful.\n"));
                    } else {
                        SwingUtilities.invokeLater(() -> logArea.append("Local Backup failed.\n"));
                    }
                }

                // 2. Google Drive Backup
                if (googleDir != null && !googleDir.isEmpty()) {
                    SwingUtilities
                            .invokeLater(() -> logArea.append("Starting Google Drive Backup to: " + googleDir + "\n"));
                    if (cloudSync.backupToDirectory(googleDir)) {
                        googleSuccess.set(true);
                        SwingUtilities.invokeLater(() -> logArea
                                .append("Google Drive Backup successful. Syncing should start automatically.\n"));
                    } else {
                        SwingUtilities.invokeLater(() -> logArea.append("Google Drive Backup failed.\n"));
                    }
                }

                String summary = "Backup Operation Completed.\n";
                if (localSuccess.get())
                    summary += "- Local: SUCCESS\n";
                if (googleSuccess.get())
                    summary += "- Google Drive: SUCCESS\n";

                String finalSummary = summary;
                SwingUtilities.invokeLater(() -> {
                    logArea.append("--------------------------------------------------\n");
                    logArea.append(finalSummary);
                    auditLogDAO.log(currentUser.getUserId(), "CLOUD_BACKUP",
                            "Backup performed. Local: " + localSuccess.get() + ", Google: " + googleSuccess.get());
                });
            }).start();
        });

        btnRestore.addActionListener(e -> {
            logArea.append("Restore functionality is currently a stub.\n");
            // Future implementation: Pick a folder and import CSVs
        });

        actionPanel.add(btnBackup);
        actionPanel.add(btnRestore);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(settingsPanel, BorderLayout.CENTER);
        topContainer.add(actionPanel, BorderLayout.SOUTH);

        panel.add(topContainer, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createAuditLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = { "ID", "User ID", "Action", "Details", "Timestamp" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Initial Load
        refreshAuditLogs(model);

        // Auto Refresh Timer (every 5 seconds)
        Timer timer = new Timer(5000, e -> refreshAuditLogs(model));
        timer.start();

        JButton btnRefresh = new JButton("Refresh Logs");
        btnRefresh.addActionListener(e -> refreshAuditLogs(model));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnRefresh);
        bottomPanel.add(new JLabel("(Auto-refreshes every 5s)"));

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAuditLogs(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<AuditLog> logs = auditLogDAO.getRecentLogs();
            for (AuditLog l : logs) {
                model.addRow(
                        new Object[] { l.getLogId(), l.getUserId(), l.getAction(), l.getDetails(), l.getTimestamp() });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "ID", "Name", "Code", "Node IP", "Active" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshDepartmentTable(model);

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshDepartmentTable(model));
        timer.start();

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Dept");
        JButton btnEdit = new JButton("Edit Dept");
        JButton btnDelete = new JButton("Delete Dept");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showDepartmentDialog(model, null));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                try {
                    Department d = departmentDAO.findById(id);
                    if (d != null)
                        showDepartmentDialog(model, d);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                try {
                    Department d = departmentDAO.findById(id);
                    if (d != null && JOptionPane.showConfirmDialog(this,
                            "Delete " + d.getDepartmentName() + "?") == JOptionPane.YES_OPTION) {
                        departmentDAO.softDelete(d.getSyncUUID());
                        refreshDepartmentTable(model);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnRefresh.addActionListener(e -> refreshDepartmentTable(model));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(new JLabel("(Auto-refreshes every 5s)"));
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshDepartmentTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Department> depts = departmentDAO.findAll();
            for (Department d : depts) {
                model.addRow(new Object[] { d.getDepartmentId(), d.getDepartmentName(), d.getDepartmentCode(),
                        d.getNodeIp(), d.isActiveFlag() });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDepartmentDialog(DefaultTableModel model, Department deptToEdit) {
        boolean isEdit = deptToEdit != null;
        JDialog dialog = new JDialog(this, isEdit ? "Edit Department" : "Add Department", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField txtName = new JTextField(isEdit ? deptToEdit.getDepartmentName() : "");
        JTextField txtCode = new JTextField(isEdit ? deptToEdit.getDepartmentCode() : "");
        JTextField txtIp = new JTextField(isEdit ? deptToEdit.getNodeIp() : "");
        JCheckBox chkActive = new JCheckBox("Active", isEdit ? deptToEdit.isActiveFlag() : true);

        dialog.add(new JLabel("Name:"));
        dialog.add(txtName);
        dialog.add(new JLabel("Code:"));
        dialog.add(txtCode);
        dialog.add(new JLabel("Node IP:"));
        dialog.add(txtIp);
        dialog.add(new JLabel("Status:"));
        dialog.add(chkActive);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                Department d = isEdit ? deptToEdit : new Department();
                d.setDepartmentName(txtName.getText());
                d.setDepartmentCode(txtCode.getText());
                d.setNodeIp(txtIp.getText());
                d.setActiveFlag(chkActive.isSelected());

                boolean success = isEdit ? departmentDAO.update(d) : departmentDAO.create(d);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Saved successfully!");
                    refreshDepartmentTable(model);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save.");
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

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
            }
            try {
                dao.RecordDAO recordDAO = new dao.RecordDAO();
                List<model.Record> records = recordDAO.findAll();
                ReportUtil.generateCSV(records, path);
                JOptionPane.showMessageDialog(this, "Report saved successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage());
            }
        }
    }

    private JPanel createActiveSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = { "ID", "User", "Host", "DB", "Command", "Time (s)", "State", "Info" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        dao.MonitoringDAO monitoringDAO = new dao.MonitoringDAO();

        // Refresh Logic
        Runnable refreshTask = () -> {
            model.setRowCount(0);
            List<model.DatabaseSession> sessions = monitoringDAO.getActiveSessions();
            for (model.DatabaseSession s : sessions) {
                model.addRow(new Object[] {
                        s.getId(), s.getUser(), s.getHost(), s.getDb(),
                        s.getCommand(), s.getTime(), s.getState(), s.getInfo()
                });
            }
        };

        // Initial Load
        refreshTask.run();

        // Auto Refresh
        Timer timer = new Timer(5000, e -> refreshTask.run());
        timer.start();

        JButton btnRefresh = new JButton("Refresh Sessions");
        btnRefresh.addActionListener(e -> refreshTask.run());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnRefresh);
        bottomPanel.add(new JLabel("(Auto-refreshes every 5s)"));

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
}