package org.example.service;

import org.example.dao.EntityDao;
import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.example.util.Validator;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class EntityService {
    private final EntityDao entityDao;
    private final Validator validator;

    public EntityService() {
        this.entityDao = new EntityDao();
        this.validator = new Validator();
    }

    public void createEntity(String name, String description) throws Exception {
        validator.validateName(name);
        validator.validateDescription(description);

        Entity entity = new Entity(name, description);
        entityDao.save(entity);
    }

    public Entity getEntity(UUID id) throws SQLException {
        return entityDao.findById(id);
    }

    public List<Entity> getAllEntities(String search, EntityStatus status, String sortBy, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        return entityDao.findAll(search, status, sortBy, offset, pageSize);
    }

    public int getTotalCount(String search, EntityStatus status) throws SQLException {
        return entityDao.count(search, status);
    }

    public void updateEntity(UUID id, String name, String description, EntityStatus status) throws Exception {
        validator.validateName(name);
        validator.validateDescription(description);

        Entity entity = entityDao.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found");
        }

        entity.setName(name);
        entity.setDescription(description);
        entity.setStatus(status);

        entityDao.update(entity);
    }

    public void deleteEntity(UUID id) throws SQLException {
        entityDao.delete(id);
    }
}
