package org.example.controller;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.example.service.EntityService;

import java.util.List;
import java.util.UUID;

public class EntityController {
    private final EntityService entityService;

    public EntityController() {
        this.entityService = new EntityService();
    }

    public void createEntity(String name, String description) throws Exception {
        entityService.createEntity(name, description);
    }

    public Entity getEntity(UUID id) throws Exception {
        return entityService.getEntity(id);
    }

    public List<Entity> getAllEntities(String search, EntityStatus status, String sortBy, int page, int pageSize) {
        try {
            return entityService.getAllEntities(search, status, sortBy, page, pageSize);
        } catch (Exception e) {
            showError("Error loading entities: " + e.getMessage());
            return List.of();
        }
    }

    public int getTotalCount(String search, EntityStatus status) {
        try {
            return entityService.getTotalCount(search, status);
        } catch (Exception e) {
            showError("Error counting entities: " + e.getMessage());
            return 0;
        }
    }

    public void updateEntity(UUID id, String name, String description, EntityStatus status) throws Exception {
        entityService.updateEntity(id, name, description, status);
    }

    public void deleteEntity(UUID id) throws Exception {
        entityService.deleteEntity(id);
    }

    private void showError(String message) {
        System.err.println(message);
    }
}