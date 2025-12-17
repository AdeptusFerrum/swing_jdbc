package org.example.dao;

import org.example.model.Entity;
import org.example.model.EntityStatus;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityDaoIntegrationTest {

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
    void testSaveAndFind() throws SQLException {
        Entity entity = new Entity("Test Entity", "Test Description");

        entityDao.save(entity);

        Entity found = entityDao.findById(entity.getId());

        assertNotNull(found);
        assertEquals(entity.getId(), found.getId());
        assertEquals("Test Entity", found.getName());
        assertEquals("Test Description", found.getDescription());
        assertEquals(EntityStatus.ACTIVE, found.getStatus());
    }

    @Test
    void testUpdate() throws SQLException {
        Entity entity = new Entity("Original", "Original Desc");
        entityDao.save(entity);

        entity.setName("Updated");
        entity.setDescription("Updated Desc");
        entity.setStatus(EntityStatus.INACTIVE);

        entityDao.update(entity);

        Entity updated = entityDao.findById(entity.getId());

        assertEquals("Updated", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals(EntityStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testDelete() throws SQLException {
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
    void testSearch() throws SQLException {
        entityDao.save(new Entity("Apple iPhone", "Smartphone"));
        entityDao.save(new Entity("Samsung Phone", "Android"));

        List<Entity> results = entityDao.findAll("phone", null, null, 0, 10);
        assertEquals(2, results.size());
    }

    @Test
    void testCount() throws SQLException {
        assertEquals(0, entityDao.count(null, null));

        entityDao.save(new Entity("Entity 1", "Desc"));
        entityDao.save(new Entity("Entity 2", "Desc"));

        assertEquals(2, entityDao.count(null, null));
    }
}