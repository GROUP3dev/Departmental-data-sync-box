package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AdminDashboard1 extends JFrame { // Class name updated

    // Define colors from the design
    private static final Color COLOR_SIDE_PANEL = new Color(52, 73, 94);
    private static final Color COLOR_SIDE_PANEL_TEXT = new Color(236, 240, 241);
    private static final Color COLOR_SIDE_PANEL_ACTIVE = new Color(44, 62, 80);
    private static final Color COLOR_HEADER = new Color(255, 255, 255);
    private static final Color COLOR_CONTENT_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_TABLE_HEADER = new Color(250, 250, 250);
    private static final Font FONT_SIDE_PANEL = new Font("Inter", Font.BOLD, 14);
    private static final Font FONT_HEADER = new Font("Inter", Font.PLAIN, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Inter", Font.BOLD, 14);
    private static final Font FONT_TABLE_CELL = new Font("Inter", Font.PLAIN, 13);
    private static final Font FONT_CARD_TITLE = new Font("Inter", Font.BOLD, 16);
    private static final Font FONT_CARD_VALUE = new Font("Inter", Font.BOLD, 28);
    private static final Font FONT_CARD_DESC = new Font("Inter", Font.PLAIN, 12);

    // Panel to hold the dynamic content
    private JPanel contentPanel;
    private CardLayout cardLayout = new CardLayout();

    public AdminDashboard1() { // Constructor name updated
        setTitle("Application Dashboard - Admin View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Side Navigation Panel ---
        add(createSidePanel(), BorderLayout.WEST);

        // --- Main Panel (Header + Content) ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_CONTENT_BACKGROUND);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createDynamicContentPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(COLOR_SIDE_PANEL);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(220, getHeight()));

        // --- Logo/Title ---
        JLabel appTitle = new JLabel("Admin Panel");
        appTitle.setForeground(COLOR_SIDE_PANEL_TEXT);
        appTitle.setFont(new Font("Inter", Font.BOLD, 22));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidePanel.add(appTitle);

        // --- Navigation Items (Updated) ---
        sidePanel.add(createNavItem("📊 Dashboard", "DASHBOARD", true)); // Default/Home View

        sidePanel.add(Box.createVerticalStrut(10)); // Spacer

        // New Admin Category
        JLabel adminLabel = new JLabel("ADMINISTRATION");
        adminLabel.setForeground(new Color(149, 165, 166));
        adminLabel.setFont(FONT_SIDE_PANEL.deriveFont(Font.BOLD, 12f));
        adminLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(adminLabel);

        // Links based on user request
        sidePanel.add(createNavItem("👤 User Management", "USERS", false));
        sidePanel.add(createNavItem("📜 System Logs", "LOGS", false));
        sidePanel.add(createNavItem("💾 Backup/Restore", "BACKUP", false));
        sidePanel.add(createNavItem("🛡 Security Monitor", "SECURITY", false));
        sidePanel.add(createNavItem("📈 Global Reports", "REPORTS", false));

        sidePanel.add(Box.createVerticalGlue()); // Pushes logout to the bottom

        sidePanel.add(createNavItem("🚪 Logout", "LOGOUT", false));
        sidePanel.add(Box.createVerticalStrut(20));

        return sidePanel;
    }

    // Updated createNavItem to handle content switching
    private JButton createNavItem(String text, String cardName, boolean active) {
        JButton navButton = new JButton(text);
        navButton.setFont(FONT_SIDE_PANEL);
        navButton.setForeground(COLOR_SIDE_PANEL_TEXT);
        navButton.setFocusPainted(false);
        navButton.setBorderPainted(false);
        navButton.setHorizontalAlignment(SwingConstants.LEFT);
        navButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navButton.setBorder(new EmptyBorder(10, 20, 10, 20));

        if (active) {
            navButton.setBackground(COLOR_SIDE_PANEL_ACTIVE);
        } else {
            navButton.setBackground(COLOR_SIDE_PANEL);
        }

        // Action Listener to switch content panel
        navButton.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            // In a real application, you'd also need logic to update all other buttons' backgrounds
        });

        // Simple hover effect
        navButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!active) navButton.setBackground(COLOR_SIDE_PANEL_ACTIVE.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!active) navButton.setBackground(COLOR_SIDE_PANEL);
            }
        });

        return navButton;
    }

    // New method to create a dynamic content area using CardLayout
    private JPanel createDynamicContentPanel() {
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CONTENT_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add the different content panels (cards)
        contentPanel.add(createDashboardPanel(), "DASHBOARD");
        contentPanel.add(createUserManagementPanel(), "USERS");
        contentPanel.add(createSimpleContentPanel("System Logs and Sync Status"), "LOGS");
        contentPanel.add(createSimpleContentPanel("Backup and Restore Utility"), "BACKUP");
        contentPanel.add(createSimpleContentPanel("Security Monitoring View"), "SECURITY");
        contentPanel.add(createGlobalReportsPanel(), "REPORTS");

        return contentPanel;
    }

    // A generic panel creator for simple views
    private JPanel createSimpleContentPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(50, 50, 50, 50)
        ));
        JLabel label = new JLabel("Content for: " + title, SwingConstants.CENTER);
        label.setFont(new Font("Inter", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // --- Specific Content Panel Methods ---

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_CONTENT_BACKGROUND);
        panel.add(createCardsPanel(), BorderLayout.NORTH);
        panel.add(createUserManagementTablePanel("Quick User Status"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("👤 Detailed User Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Add a more detailed user table
        panel.add(createUserManagementTablePanel("All System Users"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGlobalReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📈 Global Reports Overview");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Sample Report Data
        String[] columnNames = {"Department", "Total Users", "Active Projects", "Last Audit"};
        Object[][] data = {
                {"Sales", "120", "15", "2025-09-01"},
                {"Engineering", "45", "3", "2025-10-15"},
                {"Finance", "30", "5", "2025-08-20"},
                {"Marketing", "75", "10", "2025-09-25"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_TABLE_CELL);
        table.setRowHeight(35);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Replaced createTablePanel with this more specific method for reuse
    private JPanel createUserManagementTablePanel(String title) {
        JPanel tableContainer = new JPanel(new BorderLayout(0, 15));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 0, 0, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel(title);
        tableTitle.setFont(new Font("Inter", Font.BOLD, 16));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        // Table Data - Modified columns and data to fit user management
        String[] columnNames = {"Name", "Email", "Role", "Last Login", "Account Status"};
        Object[][] data = {
                {"Mike Bhand", "mikebhand@app.com", "Admin", "Today, 10:30 AM", "ACTIVE"},
                {"Andrew Strauss", "andrewstrauss@app.com", "Staff", "Yesterday", "ACTIVE"},
                {"Ross Kopelman", "rosskopelman@app.com", "Student", "2 weeks ago", "INACTIVE"},
                {"Mike Hussy", "mikehussy@app.com", "Student", "3 days ago", "ACTIVE"},
                {"Kevin Pietersen", "kevinpietersen@app.com", "Staff", "Today, 11:00 AM", "PENDING"},
                {"Ben Stokes", "bstokes@app.com", "Student", "Yesterday", "ACTIVE"},
                {"Joe Root", "jroot@app.com", "Admin", "1 hour ago", "ACTIVE"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_TABLE_CELL);
        table.setRowHeight(40);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        table.getTableHeader().setBorder(null);
        table.setSelectionBackground(new Color(230, 245, 255));
        table.getColumn("Account Status").setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    private JPanel createCardsPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(COLOR_CONTENT_BACKGROUND);

        cardsPanel.add(new GradientRoundedPanel(
                new Color(108, 92, 231), new Color(162, 155, 254),
                "👤", "Total Users", "1,240", "Staff: 24, Students: 1200"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(186, 115, 237), new Color(223, 154, 255),
                "🔒", "Pending Approvals", "15", "New registrations awaiting review"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(253, 203, 110), new Color(254, 211, 142),
                "📡", "Sync Status", "Online", "Last sync: 2 minutes ago"
        ));

        return cardsPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JTextField searchField = new JTextField("  🔍 Search ...");
        searchField.setFont(FONT_HEADER);
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        headerPanel.add(searchField, BorderLayout.CENTER);
        JLabel menuIcon = new JLabel("☰");
        menuIcon.setFont(new Font("Inter", Font.BOLD, 24));
        menuIcon.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(menuIcon, BorderLayout.EAST);
        return headerPanel;
    }

    // --- Main Method and Custom Components ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard1().setVisible(true); // Class instantiation updated
        });
    }

    static class GradientRoundedPanel extends JPanel {
        private final Color color1;
        private final Color color2;
        private static final Font FONT_CARD_TITLE = new Font("Inter", Font.BOLD, 16);
        private static final Font FONT_CARD_VALUE = new Font("Inter", Font.BOLD, 28);
        private static final Font FONT_CARD_DESC = new Font("Inter", Font.PLAIN, 12);

        public GradientRoundedPanel(Color c1, Color c2, String icon, String title, String value, String description) {
            this.color1 = c1;
            this.color2 = c2;
            setOpaque(false);
            setLayout(new GridLayout(4, 1));
            setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 30));
            iconLabel.setForeground(Color.WHITE);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(FONT_CARD_TITLE);
            titleLabel.setForeground(Color.WHITE);

            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(FONT_CARD_VALUE);
            valueLabel.setForeground(Color.WHITE);

            JLabel descLabel = new JLabel(description);
            descLabel.setFont(FONT_CARD_DESC);
            descLabel.setForeground(new Color(255, 255, 255, 180));

            add(titleLabel);
            add(valueLabel);
            add(iconLabel);
            add(descLabel);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, w, h, 15, 15);
        }
    }

    static class StatusCellRenderer extends JButton implements TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFont(new Font("Inter", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            String status = value.toString().toUpperCase();

            switch(status) {
                case "ACTIVE":
                    setBackground(new Color(220, 255, 220));
                    setForeground(new Color(0, 128, 0));
                    break;
                case "PENDING":
                    setBackground(new Color(255, 240, 210));
                    setForeground(new Color(200, 120, 0));
                    break;
                case "INACTIVE":
                    setBackground(new Color(255, 220, 220));
                    setForeground(new Color(180, 0, 0));
                    break;
                default:
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                    break;
            }
            return this;
        }
    }
}