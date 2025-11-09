package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StudentUserDashboard extends JFrame {

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

    public StudentUserDashboard() {
        setTitle("Application Dashboard - Student/User View");
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

    // --- Side Panel: Updated for Student/User Role ---
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(COLOR_SIDE_PANEL);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(220, getHeight()));

        // --- Logo/Title ---
        JLabel appTitle = new JLabel("Student Portal");
        appTitle.setForeground(COLOR_SIDE_PANEL_TEXT);
        appTitle.setFont(new Font("Inter", Font.BOLD, 22));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidePanel.add(appTitle);

        // --- Navigation Items (Student Specific) ---
        sidePanel.add(createNavItem("🏠 User Dashboard", "DASHBOARD", true)); 
        
        sidePanel.add(Box.createVerticalStrut(10)); 

        // User Category
        JLabel userLabel = new JLabel("MY DATA & TOOLS");
        userLabel.setForeground(new Color(149, 165, 166));
        userLabel.setFont(FONT_SIDE_PANEL.deriveFont(Font.BOLD, 12f));
        userLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(userLabel);

        // Links based on Student/User requirements
        sidePanel.add(createNavItem("📚 Academic Records", "RECORDS", false)); // View personal academic records
        sidePanel.add(createNavItem("✅ Data Verification", "VERIFICATION", false)); // Verify data sync results
        sidePanel.add(createNavItem("📧 Report Discrepancy", "DISCREPANCY", false)); // Report data discrepancies
        sidePanel.add(createNavItem("📢 Announcements", "ANNOUNCEMENTS", false)); // Check updates on academic/dept info

        sidePanel.add(Box.createVerticalGlue()); 

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
        contentPanel.add(createAcademicRecordsPanel(), "RECORDS");
        contentPanel.add(createDataVerificationPanel(), "VERIFICATION");
        contentPanel.add(createSimpleContentPanel("Report Data Discrepancy Form"), "DISCREPANCY");
        contentPanel.add(createAnnouncementsPanel(), "ANNOUNCEMENTS");

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
    
    // --- Specific Content Panel Methods (Student/User) ---
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_CONTENT_BACKGROUND);
        panel.add(createUserCardsPanel(), BorderLayout.NORTH);
        panel.add(createCourseProgressTable("Current Course Progress"), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createUserCardsPanel() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(COLOR_CONTENT_BACKGROUND);

        // Student-specific metrics
        cardsPanel.add(new GradientRoundedPanel(
                new Color(39, 174, 96), new Color(46, 204, 113), // Green for GPA
                "🥇", "Current GPA", "3.75", "15 credit hours completed"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(52, 152, 219), new Color(41, 128, 185), // Blue for schedule
                "📅", "Next Class", "Database Systems", "Today at 2:00 PM (Room C101)"
        ));
        cardsPanel.add(new GradientRoundedPanel(
                new Color(243, 156, 18), new Color(230, 126, 34), // Orange for action
                "❓", "Discrepancy Tickets", "2 Open", "Last submission: 2 days ago"
        ));

        return cardsPanel;
    }
    
    private JPanel createAcademicRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("📚 Full Academic Record Transcript");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        panel.add(createCourseProgressTable("All Completed and Current Courses"), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCourseProgressTable(String title) {
        JPanel tableContainer = new JPanel(new BorderLayout(0, 15));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 0, 0, 0),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel(title);
        tableTitle.setFont(new Font("Inter", Font.BOLD, 16));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        // Sample Data for Student/User view
        String[] columnNames = {"Course Code", "Course Name", "Semester", "Grade", "Status"};
        Object[][] data = {
                {"CS101", "Intro to Programming", "Fall 2024", "A-", "COMPLETED"},
                {"MA202", "Calculus II", "Fall 2024", "B+", "COMPLETED"},
                {"BA310", "Business Law", "Spring 2025", "N/A", "IN PROGRESS"},
                {"DS400", "Data Structures", "Spring 2025", "N/A", "IN PROGRESS"},
                {"HI100", "World History", "Summer 2025", "N/A", "SCHEDULED"},
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
        
        table.getColumn("Status").setCellRenderer(new CourseStatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }
    
    private JPanel createDataVerificationPanel() {
        // Content for verifying data synchronization results
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("✅ Data Verification Status");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        JTextArea verificationArea = new JTextArea("Last verification run: October 21, 2025, 11:30 AM\n\n" +
                "Personal Info Sync: SUCCESS (Verified 5 fields)\n" +
                "Financial Data Sync: SUCCESS (Verified 2 records)\n" +
                "Academic Records Sync: WARNING (1 record difference with Registry, check your grade for DS400)\n\n" +
                "If the data above is incorrect, please use the 'Report Discrepancy' link.");
        verificationArea.setFont(new Font("Inter", Font.PLAIN, 14));
        verificationArea.setEditable(false);
        verificationArea.setLineWrap(true);
        verificationArea.setWrapStyleWord(true);
        
        panel.add(new JScrollPane(verificationArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnnouncementsPanel() {
        return createSimpleContentPanel("Academic & Departmental Announcements (Check for registration deadlines)");
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JTextField searchField = new JTextField("  🔍 Search Courses or Grades...");
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
            new StudentUserDashboard().setVisible(true);
        });
    }

    // Custom renderer for the course status
    static class CourseStatusCellRenderer extends JButton implements TableCellRenderer {
        public CourseStatusCellRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setFont(new Font("Inter", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            String status = value.toString().toUpperCase();

            switch(status) {
                case "COMPLETED":
                    setBackground(new Color(220, 255, 220)); // Light Green (Success)
                    setForeground(new Color(0, 128, 0));
                    break;
                case "IN PROGRESS":
                    setBackground(new Color(210, 230, 255)); // Light Blue (Active)
                    setForeground(new Color(0, 50, 150));
                    break;
                case "SCHEDULED":
                    setBackground(new Color(255, 240, 210)); // Light Yellow (Upcoming)
                    setForeground(new Color(200, 120, 0));
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