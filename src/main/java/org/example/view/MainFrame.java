package org.example.view;

import org.example.controller.EntityController;
import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.example.view.components.PaginationPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final EntityController controller;
    private final TableModel tableModel;
    private final JTable table;
    private final PaginationPanel paginationPanel;
    private final JTextField searchField;
    private final JComboBox<EntityStatus> filterComboBox;
    private final JComboBox<String> sortComboBox;
    private final int pageSize = 10;

    public MainFrame() {
        this.controller = new EntityController();

        setTitle("CRUD Application - Entity Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        List<Entity> initialEntities = controller.getAllEntities("", null, "createdAt", 1, pageSize);
        tableModel = new TableModel(initialEntities);
        table = new JTable(tableModel);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Add New");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addEntity());
        editButton.addActionListener(e -> editEntity());
        deleteButton.addActionListener(e -> deleteEntity());
        refreshButton.addActionListener(e -> refreshTable());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Status:"));
        filterComboBox = new JComboBox<>(EntityStatus.values());
        filterComboBox.insertItemAt(null, 0);
        filterComboBox.setSelectedIndex(0);
        filterComboBox.addActionListener(e -> refreshTable());
        filterPanel.add(filterComboBox);

        filterPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Name", "Created Date", "Updated Date"});
        sortComboBox.addActionListener(e -> refreshTable());
        filterPanel.add(sortComboBox);

        paginationPanel = new PaginationPanel(this::refreshTable);

        setLayout(new BorderLayout());

        add(toolBar, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.SOUTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.PAGE_END);

        updatePagination();
    }

    private void addEntity() {
        EntityDialog dialog = new EntityDialog(this, "Add New Entity", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                controller.createEntity(dialog.getName(), dialog.getDescription());
                JOptionPane.showMessageDialog(this, "Entity created successfully!");
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error creating entity: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editEntity() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an entity to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Entity entity = tableModel.getEntityAt(selectedRow);
        EntityDialog dialog = new EntityDialog(this, "Edit Entity", entity);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                controller.updateEntity(
                        entity.getId(),
                        dialog.getName(),
                        dialog.getDescription(),
                        dialog.getStatus()
                );
                JOptionPane.showMessageDialog(this, "Entity updated successfully!");
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating entity: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEntity() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an entity to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Entity entity = tableModel.getEntityAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete entity: " + entity.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteEntity(entity.getId());
                JOptionPane.showMessageDialog(this, "Entity deleted successfully!");
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting entity: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        String search = searchField.getText().trim();
        EntityStatus status = (EntityStatus) filterComboBox.getSelectedItem();
        String sortBy = getSortByValue();
        int page = paginationPanel.getCurrentPage();

        List<Entity> entities = controller.getAllEntities(search, status, sortBy, page, pageSize);
        tableModel.updateData(entities);
        updatePagination();
    }

    private void updatePagination() {
        String search = searchField.getText().trim();
        EntityStatus status = (EntityStatus) filterComboBox.getSelectedItem();
        int totalItems = controller.getTotalCount(search, status);
        paginationPanel.setTotalItems(totalItems, pageSize);
    }

    private String getSortByValue() {
        String selected = (String) sortComboBox.getSelectedItem();
        switch (selected) {
            case "Name":
                return "name";
            case "Created Date":
                return "createdAt";
            case "Updated Date":
                return "updatedAt";
            default:
                return "createdAt";
        }
    }
}