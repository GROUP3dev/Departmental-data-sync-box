package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RegistryDashboard extends JFrame { // Renamed the class for Registry role

    // Define colors from the design (kept consistent)
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

    public RegistryDashboard() {
        setTitle("Application Dashboard - Registry Staff View");
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

    // --- Side Panel: Updated for Registry/Staff Role ---
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(COLOR_SIDE_PANEL);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(220, getHeight()));

        // --- Logo/Title ---
        JLabel appTitle = new JLabel("Registry Portal");
        appTitle.setForeground(COLOR_SIDE_PANEL_TEXT);
        appTitle.setFont(new Font("Inter", Font.BOLD, 22));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidePanel.add(appTitle);

        // --- Navigation Items (Registry Specific) ---
        sidePanel.add(createNavItem("🏠 Dashboard", "DASHBOARD", true)); // Default/Home View

        sidePanel.add(Box.createVerticalStrut(10)); // Spacer

        // Registry Category
        JLabel registryLabel = new JLabel("REGISTRY OPERATIONS");
        registryLabel.setForeground(new Color(149, 165, 166));
        registryLabel.setFont(FONT_SIDE_PANEL.deriveFont(Font.BOLD, 12f));
        registryLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        registryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(registryLabel);

        // Links based on the Staff/Registry requirements
        sidePanel.add(createNavItem("📝 Student Records", "RECORDS", false)); // Upload and edit student/dept data
        sidePanel.add(createNavItem("🔗 Sync Management", "SYNC", false));    // Trigger or monitor sync
        sidePanel.add(createNavItem("🤝 Shared Data View", "SHARED", false));  // View records from other departments
        sidePanel.add(createNavItem("🏷️ Change Logs", "LOGS", false));         // Check logs of changes

        sidePanel.add(Box.createVerticalGlue()); // Pushes logout to the bottom

        sidePanel.add(createNavItem("🚪 Logout", "LOGOUT", false));
        sidePanel.add(Box.createVerticalStrut(20));

        return sidePanel;
    }

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

        navButton.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
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

    // --- Dynamic Content Panel Setup ---
    private JPanel createDynamicContentPanel() {
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_CONTENT_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add the different content panels (cards)
        contentPanel.add(createDashboardPanel(), "DASHBOARD");
        contentPanel.add(createStudentRecordsPanel(), "RECORDS");
        contentPanel.add(createSyncManagementPanel(), "SYNC");
        contentPanel.add(createSharedDataPanel(), "SHARED");
        contentPanel.add(createChangeLogsPanel(), "LOGS");

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

    // --- Specific Content Panel Methods (Registry) ---

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_CONTENT_BACKGROUND);
        panel.add(createRegistryCardsPanel(), BorderLayout.NORTH);
        panel.add(createRecordsTablePanel("Recent Student Activity"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRegistryCardsPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(COLOR_CONTENT_BACKGROUND);

        // Registry-specific metrics
        cardsPanel.add(new GradientRoundedPanel(
                new Color(39, 174, 96), new Color(46, 204, 113), // Green for student data/completion
                "🎓", "Total Students", "1,200", "New Enrollments: 45"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(52, 152, 219), new Color(41, 128, 185), // Blue for tasks
                "📤", "Records Pending Upload", "32", "Needs review by staff"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(243, 156, 18), new Color(230, 126, 34), // Orange for shared data/sync
                "🔄", "Last Sync Status", "Success", "Synced with Finance Dept: 10 mins ago"
        ));

        return cardsPanel;
    }

    private JPanel createStudentRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📝 Student Record Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        panel.add(createRecordsTablePanel("Current Departmental Student List"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecordsTablePanel(String title) {
        JPanel tableContainer = new JPanel(new BorderLayout(0, 15));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 0, 0, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel(title);
        tableTitle.setFont(new Font("Inter", Font.BOLD, 16));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        // Sample Data for Registry/Staff view
        String[] columnNames = {"Student ID", "Name", "Program", "Record Status", "Last Edit"};
        Object[][] data = {
                {"S00123", "Alice Johnson", "Computer Science", "COMPLETE", "Today, 10:15"},
                {"S00456", "Bob Smith", "Mechanical Eng.", "INCOMPLETE", "Yesterday"},
                {"S00789", "Charlie Brown", "Business Admin.", "COMPLETE", "1 week ago"},
                {"S01011", "Diana Prince", "Architecture", "COMPLETE", "Today, 09:00"},
                {"S01213", "Evan Daniels", "Data Science", "PENDING_VERIF", "1 hour ago"},
                {"S01415", "Fiona Glen", "Biology", "COMPLETE", "3 days ago"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_TABLE_CELL);
        table.setRowHeight(40);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        table.getTableHeader().setBorder(null);
        table.setSelectionBackground(new Color(230, 245, 255));

        // Use a modified StatusCellRenderer for Record Status
        table.getColumn("Record Status").setCellRenderer(new RecordStatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    private JPanel createSyncManagementPanel() {
        return createSimpleContentPanel("Sync Management: Initiate and Monitor Data Synchronization");
    }

    private JPanel createSharedDataPanel() {
        return createSimpleContentPanel("Shared Data View: Records from Finance and Admissions");
    }

    private JPanel createChangeLogsPanel() {
        return createSimpleContentPanel("Change Logs: Audit Trail of Record Updates");
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JTextField searchField = new JTextField("  🔍 Search Student ID or Name...");
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
            new RegistryDashboard().setVisible(true);
        });
    }

    // Custom renderer for the new record statuses
    static class RecordStatusCellRenderer extends JButton implements TableCellRenderer {
        public RecordStatusCellRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFont(new Font("Inter", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            String status = value.toString().toUpperCase();

            switch(status) {
                case "COMPLETE":
                    setBackground(new Color(220, 255, 220)); // Light Green
                    setForeground(new Color(0, 128, 0));
                    break;
                case "PENDING_VERIF":
                    setBackground(new Color(255, 240, 210)); // Light Yellow/Orange
                    setForeground(new Color(200, 120, 0));
                    break;
                case "INCOMPLETE":
                    setBackground(new Color(255, 220, 220)); // Light Red
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

    // GradientRoundedPanel (kept from previous code)
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
}