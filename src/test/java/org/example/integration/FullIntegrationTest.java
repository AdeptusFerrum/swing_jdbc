package org.example.integration;

import org.example.controller.EntityController;
import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FullIntegrationTest {

    private EntityController controller;

    @BeforeAll
    void setup() {
        controller = new EntityController();
    }

    @Test
    @Order(1)
    void testFullCRUDWorkflow() throws Exception {
        controller.createEntity("Интеграционный тест 1", "Первая тестовая сущность");
        controller.createEntity("Интеграционный тест 2", "Вторая тестовая сущность");
        controller.createEntity("Другая сущность", "Для поиска");

        List<Entity> all = controller.getAllEntities(null, null, null, 1, 10);
        assertTrue(all.size() >= 3);

        List<Entity> searchResults = controller.getAllEntities("Интеграционный", null, null, 1, 10);
        assertTrue(searchResults.size() >= 2);

        Entity toUpdate = all.get(0);
        controller.updateEntity(toUpdate.getId(), "Обновленная сущность",
                "Новое описание", EntityStatus.INACTIVE);

        Entity updated = controller.getEntity(toUpdate.getId());
        assertEquals("Обновленная сущность", updated.getName());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());

        controller.deleteEntity(toUpdate.getId());

        List<Entity> afterDelete = controller.getAllEntities(null, null, null, 1, 10);
        assertTrue(afterDelete.size() >= 2);
    }

    @Test
    @Order(2)
    void testPaginationAndSorting() throws Exception {
        List<Entity> existing = controller.getAllEntities(null, null, null, 1, 100);
        for (Entity entity : existing) {
            controller.deleteEntity(entity.getId());
        }

        String[] names = {"Яблоко", "Банан", "Апельсин", "Груша", "Киви"};
        for (String name : names) {
            controller.createEntity(name, "Фрукт: " + name);
        }

        List<Entity> page1 = controller.getAllEntities(null, null, null, 1, 2);
        List<Entity> page2 = controller.getAllEntities(null, null, null, 2, 2);
        List<Entity> page3 = controller.getAllEntities(null, null, null, 3, 2);

        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertEquals(1, page3.size());

        List<Entity> sorted = controller.getAllEntities(null, null, "name", 1, 10);
        if (sorted.size() >= 2) {
            assertTrue(sorted.get(0).getName().compareTo(sorted.get(1).getName()) <= 0);
        }
    }

    @Test
    @Order(3)
    void testValidation() {
        Exception e1 = assertThrows(Exception.class, () -> {
            controller.createEntity("AB", "Valid");
        });
        assertTrue(e1.getMessage().contains("between 3 and 50"));

        String longDesc = "A".repeat(300);
        Exception e2 = assertThrows(Exception.class, () -> {
            controller.createEntity("Valid Name", longDesc);
        });
        assertTrue(e2.getMessage().contains("cannot exceed 255"));
    }

    @Test
    @Order(4)
    void testStatusWorkflow() throws Exception {
        controller.createEntity("Рабочая задача", "Нужно выполнить");

        List<Entity> tasks = controller.getAllEntities("Рабочая", null, null, 1, 10);
        assertFalse(tasks.isEmpty());

        Entity task = tasks.get(0);

        controller.updateEntity(task.getId(), "Приостановлена", "Временно отложена",
                EntityStatus.INACTIVE);

        Entity inactive = controller.getEntity(task.getId());
        assertEquals(EntityStatus.INACTIVE, inactive.getStatus());

        controller.updateEntity(task.getId(), "Архивирована", "Завершена и архив",
                EntityStatus.ARCHIVED);

        Entity archived = controller.getEntity(task.getId());
        assertEquals(EntityStatus.ARCHIVED, archived.getStatus());

        List<Entity> archivedList = controller.getAllEntities(null, EntityStatus.ARCHIVED, null, 1, 10);
        assertTrue(archivedList.stream().anyMatch(e -> e.getId().equals(task.getId())));
    }

    @AfterAll
    void cleanup() throws Exception {
    }
}