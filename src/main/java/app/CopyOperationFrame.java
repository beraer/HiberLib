package app;

import manager.ManageCopy;
import table.Book;
import table.Copy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CopyOperationFrame extends JDialog {
    private final Book book;
    private final ManageCopy manageCopy;
    private boolean copyUpdated = false;

    private JTable copiesTable;
    private DefaultTableModel copiesModel;

    public CopyOperationFrame(JFrame parent, Book book, ManageCopy manageCopy) {
        super(parent, "Manage Copies for \"" + book.getTitle() + "\"", true);
        this.book = book;
        this.manageCopy = manageCopy;

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadCopiesData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model for copies
        String[] columns = {"ID", "Copy Number", "Status"};
        copiesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        copiesTable = new JTable(copiesModel);
        copiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane for table
        JScrollPane scrollPane = new JScrollPane(copiesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCopyButton = new JButton("Add Copy");
        JButton editCopyButton = new JButton("Edit Copy");
        JButton deleteCopyButton = new JButton("Delete Copy");

        buttonPanel.add(addCopyButton);
        buttonPanel.add(editCopyButton);
        buttonPanel.add(deleteCopyButton);

        // Add action listeners
        addCopyButton.addActionListener(e -> showAddCopyDialog());
        editCopyButton.addActionListener(e -> showEditCopyDialog());
        deleteCopyButton.addActionListener(e -> deleteSelectedCopy());

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Add main panel to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadCopiesData() {
        copiesModel.setRowCount(0);
        List<Copy> copies = manageCopy.getCopiesByBook(book);
        for (Copy copy : copies) {
            Object[] row = {
                    copy.getId(),
                    copy.getCopyNumber(),
                    copy.getStatus()
            };
            copiesModel.addRow(row);
        }
    }

    private void showAddCopyDialog() {
        CopyFrame dialog = new CopyFrame(this, null, manageCopy, book);
        dialog.setVisible(true);
        if (dialog.isCopySaved()) {
            copyUpdated = true;
            loadCopiesData();
        }
    }

    private void showEditCopyDialog() {
        int selectedRow = copiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a copy to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer copyId = (Integer) copiesModel.getValueAt(selectedRow, 0);
        manageCopy.getCopyById(copyId).ifPresent(copy -> {
            CopyFrame dialog = new CopyFrame(this, copy, manageCopy, book);
            dialog.setVisible(true);
            if (dialog.isCopySaved()) {
                copyUpdated = true;
                loadCopiesData();
            }
        });
    }

    private void deleteSelectedCopy() {
        int selectedRow = copiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a copy to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer copyId = (Integer) copiesModel.getValueAt(selectedRow, 0);
        String copyNumber = copiesModel.getValueAt(selectedRow, 1).toString();
        String status = copiesModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete copy number " + copyNumber + " (ID: " + copyId + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                manageCopy.deleteCopy(copyId);
                copyUpdated = true;
                loadCopiesData();
                JOptionPane.showMessageDialog(this, "Copy deleted successfully", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting copy: " + e.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isCopyUpdated() {
        return copyUpdated;
    }
}
