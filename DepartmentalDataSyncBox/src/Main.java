import ui.LoginUI;
import sync.SyncManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Start Sync Manager (Background Service)
        SyncManager.getInstance().startPeriodicSync();

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LoginUI().setVisible(true);
        });
    }
}