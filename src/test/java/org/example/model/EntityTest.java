package org.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testEntityCreation() {
        Entity entity = new Entity();
        assertNotNull(entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
    }

    @Test
    void testEntityWithParameters() {
        Entity entity = new Entity("Test Name", "Test Description");
        assertEquals("Test Name", entity.getName());
        assertEquals("Test Description", entity.getDescription());
        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
    }

    @Test
    void testSetters() {
        Entity entity = new Entity();
        UUID id = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        entity.setId(id);
        entity.setName("New Name");
        entity.setDescription("New Desc");
        entity.setStatus(EntityStatus.INACTIVE);
        entity.setCreatedAt(time.minusDays(1));
        entity.setUpdatedAt(time);

        assertEquals(id, entity.getId());
        assertEquals("New Name", entity.getName());
        assertEquals("New Desc", entity.getDescription());
        assertEquals(EntityStatus.INACTIVE, entity.getStatus());
        assertEquals(time.minusDays(1), entity.getCreatedAt());
        assertEquals(time, entity.getUpdatedAt());
    }

    @Test
    void testToString() {
        Entity entity = new Entity("Test", "Description");
        String result = entity.toString();
        assertTrue(result.contains("Test"));
        assertTrue(result.contains("Description"));
        assertTrue(result.contains("ACTIVE"));
    }
}