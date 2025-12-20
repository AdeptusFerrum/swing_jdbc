package org.example.dao;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityDaoTest {

    private EntityDao entityDao;

    @BeforeAll
    void setupAll() throws SQLException {
        entityDao = new EntityDao();
    }

    @BeforeEach
    void clearDatabase() throws SQLException {
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM entities");
        }
    }

    @Test
    void testSaveEntity() throws SQLException {
        Entity entity = new Entity("Test Save", "Test Description");

        entityDao.save(entity);

        Entity found = entityDao.findById(entity.getId());

        assertNotNull(found);
        assertEquals(entity.getId(), found.getId());
        assertEquals("Test Save", found.getName());
        assertEquals("Test Description", found.getDescription());
        assertEquals(EntityStatus.ACTIVE, found.getStatus());
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        UUID fakeId = UUID.randomUUID();

        Entity result = entityDao.findById(fakeId);

        assertNull(result);
    }

    @Test
    void testUpdateEntity() throws SQLException {
        Entity entity = new Entity("Original", "Original Desc");
        entityDao.save(entity);

        entity.setName("Updated Name");
        entity.setDescription("Updated Desc");
        entity.setStatus(EntityStatus.INACTIVE);

        entityDao.update(entity);

        Entity updated = entityDao.findById(entity.getId());

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testDeleteEntity() throws SQLException {
        Entity entity = new Entity("To Delete", "Description");
        entityDao.save(entity);

        entityDao.delete(entity.getId());

        Entity deleted = entityDao.findById(entity.getId());
        assertNull(deleted);
    }

    @Test
    void testFindAll() throws SQLException {
        entityDao.save(new Entity("Entity 1", "Desc 1"));
        entityDao.save(new Entity("Entity 2", "Desc 2"));

        List<Entity> all = entityDao.findAll(null, null, null, 0, 10);

        assertEquals(2, all.size());
    }

    @Test
    void testFindAllWithSearch() throws SQLException {
        entityDao.save(new Entity("Apple iPhone", "Smartphone"));
        entityDao.save(new Entity("Samsung Phone", "Android phone"));
        entityDao.save(new Entity("MacBook", "Laptop"));

        List<Entity> phoneResults = entityDao.findAll("phone", null, null, 0, 10);
        assertEquals(2, phoneResults.size());

        List<Entity> appleResults = entityDao.findAll("Apple", null, null, 0, 10);
        assertEquals(1, appleResults.size());
    }

    @Test
    void testFindAllWithStatusFilter() throws SQLException {
        Entity active1 = new Entity("Active 1", "Desc");
        active1.setStatus(EntityStatus.ACTIVE);

        Entity active2 = new Entity("Active 2", "Desc");
        active2.setStatus(EntityStatus.ACTIVE);

        Entity inactive = new Entity("Inactive", "Desc");
        inactive.setStatus(EntityStatus.INACTIVE);

        entityDao.save(active1);
        entityDao.save(active2);
        entityDao.save(inactive);

        List<Entity> activeResults = entityDao.findAll(null, EntityStatus.ACTIVE, null, 0, 10);
        List<Entity> inactiveResults = entityDao.findAll(null, EntityStatus.INACTIVE, null, 0, 10);

        assertEquals(2, activeResults.size());
        assertEquals(1, inactiveResults.size());
    }

    @Test
    void testFindAllWithSorting() throws SQLException {
        entityDao.save(new Entity("Charlie", "C"));
        entityDao.save(new Entity("Alpha", "A"));
        entityDao.save(new Entity("Bravo", "B"));

        List<Entity> byName = entityDao.findAll(null, null, "name", 0, 10);
        if (byName.size() >= 3) {
            assertEquals("Alpha", byName.get(0).getName());
            assertEquals("Bravo", byName.get(1).getName());
            assertEquals("Charlie", byName.get(2).getName());
        }
    }

    @Test
    void testFindAllWithPagination() throws SQLException {
        for (int i = 1; i <= 15; i++) {
            entityDao.save(new Entity("Entity " + i, "Description " + i));
        }

        List<Entity> page1 = entityDao.findAll(null, null, null, 0, 5);
        List<Entity> page2 = entityDao.findAll(null, null, null, 5, 5);
        List<Entity> page3 = entityDao.findAll(null, null, null, 10, 5);

        assertEquals(5, page1.size());
        assertEquals(5, page2.size());
        assertEquals(5, page3.size());
    }

    @Test
    void testCount() throws SQLException {
        int initialCount = entityDao.count(null, null);

        entityDao.save(new Entity("Test 1", "Desc"));
        entityDao.save(new Entity("Test 2", "Desc"));

        int newCount = entityDao.count(null, null);
        assertEquals(initialCount + 2, newCount);
    }

    @Test
    void testCountWithSearch() throws SQLException {
        entityDao.save(new Entity("Apple iPhone", "Smartphone"));
        entityDao.save(new Entity("Samsung Phone", "Android"));
        entityDao.save(new Entity("MacBook", "Laptop"));

        int phoneCount = entityDao.count("phone", null);
        assertEquals(2, phoneCount);

        int appleCount = entityDao.count("Apple", null);
        assertEquals(1, appleCount);
    }

    @Test
    void testCountWithStatus() throws SQLException {
        Entity active = new Entity("Active", "Desc");
        active.setStatus(EntityStatus.ACTIVE);

        Entity inactive = new Entity("Inactive", "Desc");
        inactive.setStatus(EntityStatus.INACTIVE);

        entityDao.save(active);
        entityDao.save(inactive);

        int activeCount = entityDao.count(null, EntityStatus.ACTIVE);
        int inactiveCount = entityDao.count(null, EntityStatus.INACTIVE);

        assertEquals(1, activeCount);
        assertEquals(1, inactiveCount);
    }

    @Test
    void testMultipleOperations() throws SQLException {
        Entity entity = new Entity("Test", "Description");

        entityDao.save(entity);

        Entity found = entityDao.findById(entity.getId());
        assertNotNull(found);

        entity.setName("Updated");
        entity.setDescription("Updated Desc");
        entityDao.update(entity);

        Entity updated = entityDao.findById(entity.getId());
        assertEquals("Updated", updated.getName());

        entityDao.delete(entity.getId());

        Entity deleted = entityDao.findById(entity.getId());
        assertNull(deleted);
    }
}