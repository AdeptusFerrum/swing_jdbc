package org.example.service;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ComplexBusinessLogicTest {

    private EntityService entityService;

    @BeforeEach
    void setUp() {
        entityService = new EntityService();
    }

    @Test
    void testCompleteEntityLifecycle() throws Exception {
        entityService.createEntity("Startup Task", "Initial description");

        List<Entity> all = entityService.getAllEntities(null, null, null, 1, 10);
        assertFalse(all.isEmpty());

        Entity entity = all.get(0);

        entityService.updateEntity(entity.getId(), "In Progress Task",
                "Working on it", EntityStatus.INACTIVE);

        Entity updated = entityService.getEntity(entity.getId());
        assertEquals("In Progress Task", updated.getName());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());

        entityService.updateEntity(entity.getId(), "Completed Task",
                "Done successfully", EntityStatus.ARCHIVED);

        Entity completed = entityService.getEntity(entity.getId());
        assertEquals("Completed Task", completed.getName());
        assertEquals(EntityStatus.ARCHIVED, completed.getStatus());

        entityService.deleteEntity(entity.getId());

        Exception e = assertThrows(Exception.class, () -> {
            entityService.getEntity(entity.getId());
        });
        assertNotNull(e);
    }

    @Test
    void testMultipleEntitiesOperations() throws Exception {
        for (int i = 1; i <= 10; i++) {
            entityService.createEntity("Task " + i, "Description " + i);
        }

        int total = entityService.getTotalCount(null, null);
        assertTrue(total >= 10);

        List<Entity> taskResults = entityService.getAllEntities("Task", null, null, 1, 10);
        assertTrue(taskResults.size() >= 10);

        for (int i = 0; i < taskResults.size(); i += 2) {
            Entity entity = taskResults.get(i);
            entityService.updateEntity(entity.getId(), "Updated " + entity.getName(),
                    "Modified", EntityStatus.INACTIVE);
        }

        List<Entity> active = entityService.getAllEntities(null, EntityStatus.ACTIVE, null, 1, 10);
        List<Entity> inactive = entityService.getAllEntities(null, EntityStatus.INACTIVE, null, 1, 10);

        assertTrue(active.size() >= 5);
        assertTrue(inactive.size() >= 5);
    }

    @Test
    void testSearchComplexQueries() throws Exception {
        entityService.createEntity("Купить молоко", "Молоко 2.5%");
        entityService.createEntity("Позвонить маме", "Обсудить праздник");
        entityService.createEntity("Заказать продукты", "Молоко, хлеб, яйца");
        entityService.createEntity("Сделать домашку", "Математика и физика");

        List<Entity> milkResults = entityService.getAllEntities("молок", null, null, 1, 10);
        assertTrue(milkResults.size() >= 2);

        List<Entity> callResults = entityService.getAllEntities("Позвонить", null, null, 1, 10);
        assertTrue(callResults.size() >= 1);

        List<Entity> allTasks = entityService.getAllEntities("о", null, null, 1, 10);
        assertTrue(allTasks.size() >= 4);
    }

    @Test
    void testAdvancedPagination() throws Exception {
        for (int i = 1; i <= 25; i++) {
            entityService.createEntity("Item " + String.format("%03d", i),
                    "Description " + i);
        }

        List<Entity> page1 = entityService.getAllEntities(null, null, "name", 1, 5);
        List<Entity> page2 = entityService.getAllEntities(null, null, "name", 2, 5);
        List<Entity> page3 = entityService.getAllEntities(null, null, "name", 3, 5);
        List<Entity> page5 = entityService.getAllEntities(null, null, "name", 5, 5);

        assertEquals(5, page1.size());
        assertEquals(5, page2.size());
        assertEquals(5, page3.size());
        assertEquals(5, page5.size());

        if (!page1.isEmpty() && !page2.isEmpty()) {
            assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
        }

        if (page1.size() >= 2) {
            assertTrue(page1.get(0).getName().compareTo(page1.get(1).getName()) < 0);
        }
    }

    @Test
    void testDataConsistency() throws Exception {
        entityService.createEntity("Важная задача", "Не забыть выполнить");

        List<Entity> all = entityService.getAllEntities(null, null, null, 1, 10);
        Entity entity = all.get(0);

        String originalName = entity.getName();
        UUID originalId = entity.getId();

        entityService.updateEntity(entity.getId(), "Обновленная задача",
                "Изменили описание", EntityStatus.INACTIVE);

        Entity updated = entityService.getEntity(originalId);
        assertEquals(originalId, updated.getId());

        assertNotEquals(originalName, updated.getName());
        assertEquals("Обновленная задача", updated.getName());

        assertEquals(EntityStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testErrorHandlingInBusinessLogic() {
        Exception e1 = assertThrows(Exception.class, () -> {
            entityService.createEntity("AB", "Valid description");
        });
        assertTrue(e1.getMessage().contains("between 3 and 50"));

        String longDesc = "A".repeat(300);
        Exception e2 = assertThrows(Exception.class, () -> {
            entityService.createEntity("Valid name", longDesc);
        });
        assertTrue(e2.getMessage().contains("cannot exceed 255"));

        Exception e3 = assertThrows(Exception.class, () -> {
            entityService.updateEntity(java.util.UUID.randomUUID(),
                    "Name", "Desc", EntityStatus.ACTIVE);
        });
        assertNotNull(e3);

        try {
            entityService.deleteEntity(java.util.UUID.randomUUID());
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}