package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class SignIn extends JFrame {

    // Define colors from the design
    private static final Color COLOR_PRIMARY_GREEN = new Color(30, 138, 94);
    private static final Color COLOR_BACKGROUND_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_INPUT_BACKGROUND = new Color(230, 245, 238);
    private static final Color COLOR_TEXT_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_TEXT_DARK = new Color(60, 63, 65);
    private static final Font FONT_MAIN = new Font("Inter", Font.BOLD, 28);
    private static final Font FONT_BODY = new Font("Inter", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Inter", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("Inter", Font.PLAIN, 16);

    public SignIn() {
        setTitle("Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        // Left Panel (Sign In Form)
        add(createSignInPanel());

        // Right Panel (Sign Up Prompt)
        add(createSignUpPromptPanel());
    }

    private JPanel createSignInPanel() {
        JPanel signInPanel = new JPanel();
        signInPanel.setBackground(COLOR_BACKGROUND_WHITE);
        signInPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;


        // "Sign In" Label
        JLabel signInLabel = new JLabel("Sign In", SwingConstants.CENTER);
        signInLabel.setFont(FONT_MAIN);
        signInLabel.setForeground(COLOR_PRIMARY_GREEN);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 30, 20);
        signInPanel.add(signInLabel, gbc);

        // Reset insets
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridwidth = 1;

        // Email Field
        gbc.gridy = 1;
        signInPanel.add(createIconTextField("✉️", "Email"), gbc);

        // Password Field
        gbc.gridy = 2;
        signInPanel.add(createIconPasswordField("🔒", "Password"), gbc);

        // "Forgot your password?" Label
        JLabel forgotPasswordLabel = new JLabel("Forgot your password?", SwingConstants.CENTER);
        forgotPasswordLabel.setFont(FONT_BODY.deriveFont(Font.PLAIN));
        forgotPasswordLabel.setForeground(Color.GRAY);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        signInPanel.add(forgotPasswordLabel, gbc);

        // "SIGN IN" Button
        JButton signInButton = new SolidButton("SIGN IN");
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 80, 10, 80);
        signInPanel.add(signInButton, gbc);

        return signInPanel;
    }

    private JPanel createSignUpPromptPanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(COLOR_PRIMARY_GREEN);
        welcomePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // "Hello, Friend!" Label
        JLabel welcomeLabel = new JLabel("Hello, Friend!", SwingConstants.CENTER);
        welcomeLabel.setFont(FONT_MAIN);
        welcomeLabel.setForeground(COLOR_TEXT_WHITE);
        gbc.gridy = 0;
        welcomePanel.add(welcomeLabel, gbc);

        // Sub-text Label
        JLabel subTextLabel = new JLabel("<html><div style='text-align: center;'>Enter your personal details<br>and start journey with us</div></html>", SwingConstants.CENTER);
        subTextLabel.setFont(FONT_BODY);
        subTextLabel.setForeground(COLOR_TEXT_WHITE);
        gbc.gridy = 1;
        welcomePanel.add(subTextLabel, gbc);

        // "SIGN UP" Button
        JButton signUpButton = new OutlinedButton("SIGN UP");
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 100, 10, 100);
        welcomePanel.add(signUpButton, gbc);

        return welcomePanel;
    }

    private JPanel createIconTextField(String icon, String placeholder) {
        JPanel panel = new RoundedPanel(15);
        panel.setBackground(COLOR_INPUT_BACKGROUND);
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FONT_INPUT);
        iconLabel.setForeground(Color.GRAY);
        panel.add(iconLabel, BorderLayout.WEST);

        PlaceholderTextField textField = new PlaceholderTextField(placeholder);
        textField.setFont(FONT_INPUT);
        textField.setForeground(COLOR_TEXT_DARK);
        textField.setBorder(null);
        textField.setOpaque(false);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIconPasswordField(String icon, String placeholder) {
        JPanel panel = new RoundedPanel(15);
        panel.setBackground(COLOR_INPUT_BACKGROUND);
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FONT_INPUT);
        iconLabel.setForeground(Color.GRAY);
        panel.add(iconLabel, BorderLayout.WEST);

        PlaceholderPasswordField passwordField = new PlaceholderPasswordField(placeholder);
        passwordField.setFont(FONT_INPUT);
        passwordField.setForeground(COLOR_TEXT_DARK);
        passwordField.setBorder(null);
        passwordField.setOpaque(false);
        passwordField.setEchoChar('●');
        panel.add(passwordField, BorderLayout.CENTER);

        return panel;
    }


    public static void main(String[] args) {
        // Set custom Look and Feel properties
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.font", FONT_BUTTON);
            UIManager.put("Label.font", FONT_BODY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the application
        SwingUtilities.invokeLater(() -> {
            new SignIn().setVisible(true);
        });
    }

    // --- Custom Components ---

    /**
     * A JTextField that shows a placeholder text when empty.
     */
    static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                g2.dispose();
            }
        }

        {
            // Add focus listener to repaint on focus gain/loss
            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }
    }

    /**
     * A JPasswordField that shows a placeholder text when empty.
     */
    static class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                g2.dispose();
            }
        }

        {
            // Add focus listener to repaint on focus gain/loss
            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }
    }


    /**
     * A JButton with a solid background and rounded corners.
     */
    static class SolidButton extends JButton {
        public SolidButton(String text) {
            super(text);
            setForeground(COLOR_TEXT_WHITE);
            setBackground(COLOR_PRIMARY_GREEN);
            setFont(FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 25, 12, 25));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 50, 50));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /**
     * A JButton with a transparent background and a rounded border.
     */
    static class OutlinedButton extends JButton {
        public OutlinedButton(String text) {
            super(text);
            setForeground(COLOR_TEXT_WHITE);
            setBackground(new Color(0, 0, 0, 0)); // Transparent
            setFont(FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 25, 10, 25));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw border
            g2.setColor(COLOR_TEXT_WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 50, 50));

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /**
     * A JPanel with rounded corners.
     */
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false); // Important for parent background to show through
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draws the rounded panel with borders.
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            graphics.setColor(getForeground());
            graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        }
    }
}
