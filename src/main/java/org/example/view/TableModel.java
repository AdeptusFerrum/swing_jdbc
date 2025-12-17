package org.example.view;

import org.example.model.Entity;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final List<Entity> entities;
    private final String[] columnNames = {"ID", "Name", "Description", "Status", "Created", "Updated"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TableModel(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public int getRowCount() {
        return entities.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entity entity = entities.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return entity.getId().toString().substring(0, 8) + "...";
            case 1:
                return entity.getName();
            case 2:
                return entity.getDescription();
            case 3:
                return entity.getStatus();
            case 4:
                return entity.getCreatedAt().format(formatter);
            case 5:
                return entity.getUpdatedAt().format(formatter);
            default:
                return null;
        }
    }

    public Entity getEntityAt(int rowIndex) {
        return entities.get(rowIndex);
    }

    public void updateData(List<Entity> newEntities) {
        entities.clear();
        entities.addAll(newEntities);
        fireTableDataChanged();
    }
}