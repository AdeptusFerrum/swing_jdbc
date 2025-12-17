package org.example.controller;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EntityControllerTest {

    private EntityController entityController;

    @BeforeEach
    void setUp() {
        entityController = new EntityController();
    }

    @Test
    void testCreateAndGetEntity() throws Exception {
        entityController.createEntity("Test Entity", "Test Description");

        List<Entity> entities = entityController.getAllEntities(null, null, null, 1, 10);
        assertFalse(entities.isEmpty());

        Entity entity = entities.get(0);
        assertEquals("Test Entity", entity.getName());
        assertEquals("Test Description", entity.getDescription());
    }

    @Test
    void testValidation() {
        Exception exception1 = assertThrows(Exception.class, () -> {
            entityController.createEntity("AB", "Valid Description");
        });
        assertTrue(exception1.getMessage().contains("between 3 and 50"));

        String longDesc = "A".repeat(256);
        Exception exception2 = assertThrows(Exception.class, () -> {
            entityController.createEntity("Valid Name", longDesc);
        });
        assertTrue(exception2.getMessage().contains("cannot exceed 255"));
    }

    @Test
    void testUpdateEntity() throws Exception {
        entityController.createEntity("Original", "Original Desc");

        List<Entity> entities = entityController.getAllEntities(null, null, null, 1, 10);
        Entity entity = entities.get(0);

        entityController.updateEntity(entity.getId(), "Updated", "Updated Desc", EntityStatus.INACTIVE);

        Entity updated = entityController.getEntity(entity.getId());
        assertEquals("Updated", updated.getName());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testDeleteEntity() throws Exception {
        entityController.createEntity("To Delete", "Description");

        List<Entity> beforeDelete = entityController.getAllEntities(null, null, null, 1, 10);
        int initialCount = beforeDelete.size();

        Entity toDelete = beforeDelete.get(0);
        entityController.deleteEntity(toDelete.getId());

        List<Entity> afterDelete = entityController.getAllEntities(null, null, null, 1, 10);
        assertEquals(initialCount - 1, afterDelete.size());
    }

    @Test
    void testSearch() throws Exception {
        entityController.createEntity("Apple iPhone", "Smartphone");
        entityController.createEntity("Samsung Galaxy", "Android phone");
        entityController.createEntity("MacBook Pro", "Laptop");

        List<Entity> phoneResults = entityController.getAllEntities("phone", null, null, 1, 10);
        assertEquals(2, phoneResults.size());

        List<Entity> appleResults = entityController.getAllEntities("Apple", null, null, 1, 10);
        assertEquals(1, appleResults.size());

        List<Entity> samsungResults = entityController.getAllEntities("Samsung", null, null, 1, 10);
        assertEquals(1, samsungResults.size());
    }

    @Test
    void testGetTotalCount() throws Exception {
        entityController.createEntity("Entity 1", "Desc");
        entityController.createEntity("Entity 2", "Desc");

        int count = entityController.getTotalCount(null, null);
        assertTrue(count >= 2);
    }

    @Test
    void testCompleteCRUDCycle() throws Exception {
        // Create
        entityController.createEntity("Test 1", "Desc 1");
        entityController.createEntity("Test 2", "Desc 2");

        // Read
        List<Entity> all = entityController.getAllEntities(null, null, null, 1, 10);
        int initialCount = all.size();

        // Update
        if (!all.isEmpty()) {
            Entity toUpdate = all.get(0);
            entityController.updateEntity(toUpdate.getId(), "Updated", "New Desc", EntityStatus.INACTIVE);

            Entity updated = entityController.getEntity(toUpdate.getId());
            assertEquals("Updated", updated.getName());
        }

        // Delete
        if (!all.isEmpty()) {
            Entity toDelete = all.get(0);
            entityController.deleteEntity(toDelete.getId());

            List<Entity> afterDelete = entityController.getAllEntities(null, null, null, 1, 10);
            assertEquals(initialCount - 1, afterDelete.size());
        }
    }

    @Test
    void testPagination() throws Exception {
        for (int i = 1; i <= 15; i++) {
            entityController.createEntity("Entity " + i, "Description " + i);
        }

        List<Entity> page1 = entityController.getAllEntities(null, null, null, 1, 5);
        List<Entity> page2 = entityController.getAllEntities(null, null, null, 2, 5);
        List<Entity> page3 = entityController.getAllEntities(null, null, null, 3, 5);

        assertEquals(5, page1.size());
        assertEquals(5, page2.size());
        assertEquals(5, page3.size());

        if (!page1.isEmpty() && !page2.isEmpty()) {
            assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
        }
    }

    @Test
    void testStatusFilters() throws Exception {
        entityController.createEntity("Active 1", "Active entity");
        entityController.createEntity("Inactive 1", "Inactive entity");

        List<Entity> all = entityController.getAllEntities(null, null, null, 1, 10);
        if (all.size() >= 2) {
            Entity second = all.get(1);
            entityController.updateEntity(second.getId(), "Inactive 1", "Inactive", EntityStatus.INACTIVE);

            List<Entity> active = entityController.getAllEntities(null, EntityStatus.ACTIVE, null, 1, 10);
            List<Entity> inactive = entityController.getAllEntities(null, EntityStatus.INACTIVE, null, 1, 10);

            assertTrue(active.size() >= 1);
            assertTrue(inactive.size() >= 1);
        }
    }

    @Test
    void testSorting() throws Exception {
        entityController.createEntity("Charlie", "C");
        Thread.sleep(10);
        entityController.createEntity("Alpha", "A");
        Thread.sleep(10);
        entityController.createEntity("Bravo", "B");

        // Сортировка по имени
        List<Entity> byName = entityController.getAllEntities(null, null, "name", 1, 10);
        if (byName.size() >= 3) {
            assertEquals("Alpha", byName.get(0).getName());
            assertEquals("Bravo", byName.get(1).getName());
            assertEquals("Charlie", byName.get(2).getName());
        }

        // Сортировка по дате создания (последние сначала)
        List<Entity> byDate = entityController.getAllEntities(null, null, "createdAt", 1, 10);
        if (byDate.size() >= 3) {
            assertEquals("Bravo", byDate.get(0).getName()); // Последний созданный
        }
    }
}