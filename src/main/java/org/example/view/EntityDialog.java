package org.example.view;

import org.example.model.Entity;
import org.example.model.EntityStatus;

import javax.swing.*;
import java.awt.*;

public class EntityDialog extends JDialog {
    private final JTextField nameField;
    private final JTextArea descriptionArea;
    private final JComboBox<EntityStatus> statusComboBox;
    private Entity entity;
    private boolean confirmed = false;

    public EntityDialog(Frame owner, String title, Entity entity) {
        super(owner, title, true);
        this.entity = entity;

        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name*:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(descriptionArea), gbc);

        if (entity != null) {
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            mainPanel.add(new JLabel("Status:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            statusComboBox = new JComboBox<>(EntityStatus.values());
            mainPanel.add(statusComboBox, gbc);
        } else {
            statusComboBox = null;
        }

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (entity != null) {
            nameField.setText(entity.getName());
            descriptionArea.setText(entity.getDescription());
            if (statusComboBox != null) {
                statusComboBox.setSelectedItem(entity.getStatus());
            }
        }

        getRootPane().setDefaultButton(okButton);
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        if (name.isEmpty() || name.length() < 3 || name.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "Name must be between 3 and 50 characters",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String description = descriptionArea.getText().trim();
        if (description.length() > 255) {
            JOptionPane.showMessageDialog(this,
                    "Description cannot exceed 255 characters",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getDescription() {
        return descriptionArea.getText().trim();
    }

    public EntityStatus getStatus() {
        return statusComboBox != null ? (EntityStatus) statusComboBox.getSelectedItem() : EntityStatus.ACTIVE;
    }
}