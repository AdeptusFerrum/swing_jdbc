package org.example.service;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EntityServiceTest {

    private EntityService entityService;

    @BeforeEach
    void setUp() {
        entityService = new EntityService();
    }

    @Test
    void testCreateEntity_ValidData() {
        assertDoesNotThrow(() -> {
            entityService.createEntity("Valid Name", "Valid Description");
        });
    }

    @Test
    void testCreateEntity_InvalidName_TooShort() {
        Exception exception = assertThrows(Exception.class, () -> {
            entityService.createEntity("AB", "Valid Description");
        });
        assertTrue(exception.getMessage().contains("between 3 and 50"));
    }

    @Test
    void testCreateEntity_InvalidName_TooLong() {
        String longName = "A".repeat(51);
        Exception exception = assertThrows(Exception.class, () -> {
            entityService.createEntity(longName, "Valid Description");
        });
        assertTrue(exception.getMessage().contains("between 3 and 50"));
    }

    @Test
    void testCreateEntity_InvalidDescription_TooLong() {
        String longDesc = "A".repeat(256);
        Exception exception = assertThrows(Exception.class, () -> {
            entityService.createEntity("Valid Name", longDesc);
        });
        assertTrue(exception.getMessage().contains("cannot exceed 255"));
    }

    @Test
    void testGetAllEntities() throws Exception {
        entityService.createEntity("Test 1", "Description 1");
        entityService.createEntity("Test 2", "Description 2");

        List<Entity> entities = entityService.getAllEntities(null, null, null, 1, 10);

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
    }

    @Test
    void testSearchEntities() throws Exception {
        entityService.createEntity("Apple iPhone", "Smartphone");
        entityService.createEntity("Samsung Phone", "Android phone");
        entityService.createEntity("MacBook", "Laptop");

        List<Entity> phoneResults = entityService.getAllEntities("phone", null, null, 1, 10);
        assertEquals(2, phoneResults.size());

        List<Entity> appleResults = entityService.getAllEntities("Apple", null, null, 1, 10);
        assertEquals(1, appleResults.size());
    }

    @Test
    void testGetTotalCount() throws Exception {
        entityService.createEntity("Entity 1", "Desc");
        entityService.createEntity("Entity 2", "Desc");

        int count = entityService.getTotalCount(null, null);
        assertTrue(count >= 2);
    }

    @Test
    void testUpdateEntity() throws Exception {
        entityService.createEntity("Original", "Original Desc");

        List<Entity> entities = entityService.getAllEntities(null, null, null, 1, 10);
        assertFalse(entities.isEmpty());

        Entity entity = entities.get(0);
        entityService.updateEntity(entity.getId(), "Updated", "Updated Desc", EntityStatus.INACTIVE);

        Entity updated = entityService.getEntity(entity.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testDeleteEntity() throws Exception {
        entityService.createEntity("To Delete", "Description");

        List<Entity> entities = entityService.getAllEntities(null, null, null, 1, 10);
        Entity entity = entities.get(0);

        entityService.deleteEntity(entity.getId());

        Exception exception = assertThrows(Exception.class, () -> {
            entityService.getEntity(entity.getId());
        });
        assertNotNull(exception);
    }

    @Test
    void testPagination() throws Exception {
        for (int i = 1; i <= 15; i++) {
            entityService.createEntity("Entity " + i, "Description " + i);
        }

        List<Entity> page1 = entityService.getAllEntities(null, null, null, 1, 5);
        List<Entity> page2 = entityService.getAllEntities(null, null, null, 2, 5);
        List<Entity> page3 = entityService.getAllEntities(null, null, null, 3, 5);

        assertEquals(5, page1.size());
        assertEquals(5, page2.size());
        assertEquals(5, page3.size());

        assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
    }

    @Test
    void testStatusFilter() throws Exception {
        entityService.createEntity("Active Entity", "Active");
        entityService.createEntity("Inactive Entity", "Inactive");

        // Обновляем вторую сущность на INACTIVE
        List<Entity> all = entityService.getAllEntities(null, null, null, 1, 10);
        Entity second = all.get(1);
        entityService.updateEntity(second.getId(), "Inactive Entity", "Inactive", EntityStatus.INACTIVE);

        List<Entity> active = entityService.getAllEntities(null, EntityStatus.ACTIVE, null, 1, 10);
        List<Entity> inactive = entityService.getAllEntities(null, EntityStatus.INACTIVE, null, 1, 10);

        assertTrue(active.size() >= 1);
        assertTrue(inactive.size() >= 1);
    }
}