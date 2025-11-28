package ui.dialogs;

import dao.SyncQueueDAO;
import model.SyncQueue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConflictResolverDialog extends JDialog {
    private final SyncQueueDAO syncQueueDAO = new SyncQueueDAO();
    private JTable table;
    private DefaultTableModel model;

    public ConflictResolverDialog(Window owner) {
        super(owner, "Conflict Resolution", ModalityType.APPLICATION_MODAL);
        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        // Table
        String[] columns = { "Queue ID", "Entity", "Operation", "Retry Count", "Error/Status" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnRetry = new JButton("Retry Selected");
        JButton btnDiscard = new JButton("Discard Selected");
        JButton btnClose = new JButton("Close");

        btnRetry.addActionListener(e -> retrySelected());
        btnDiscard.addActionListener(e -> discardSelected());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnRetry);
        btnPanel.add(btnDiscard);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<SyncQueue> failedItems = syncQueueDAO.getFailedItems();
        if (failedItems.isEmpty()) {
            // Optional: Show a message or just empty table
        }
        for (SyncQueue item : failedItems) {
            model.addRow(new Object[] {
                    item.getQueueId(),
                    item.getEntityName(),
                    item.getOperationType(),
                    item.getRetryCount(),
                    item.getStatus()
            });
        }
    }

    private void retrySelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to retry.");
            return;
        }

        long queueId = (long) model.getValueAt(row, 0);
        if (syncQueueDAO.updateStatus(queueId, "PENDING")) {
            JOptionPane.showMessageDialog(this, "Item queued for retry.");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update item status.");
        }
    }

    private void discardSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to discard.");
            return;
        }

        long queueId = (long) model.getValueAt(row, 0);
        // We can mark it as COMPLETED (ignored) or delete it.
        // Let's mark as COMPLETED with a note or just COMPLETED to stop retries.
        // Or maybe a new status "DISCARDED" if the schema allows, but "COMPLETED" is
        // safer for now if enum restricted.
        // Assuming 'COMPLETED' is fine.
        if (syncQueueDAO.updateStatus(queueId, "COMPLETED")) {
            JOptionPane.showMessageDialog(this, "Item discarded (marked as COMPLETED).");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update item status.");
        }
    }
}