package org.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testDefaultConstructor() {
        Entity entity = new Entity();
        assertNotNull(entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
    }

    @Test
    void testParameterizedConstructor() {
        Entity entity = new Entity("Test", "Description");
        assertEquals("Test", entity.getName());
        assertEquals("Description", entity.getDescription());
        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
    }

    @Test
    void testSetters() {
        Entity entity = new Entity();
        UUID id = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        entity.setId(id);
        entity.setName("Name");
        entity.setDescription("Desc");
        entity.setStatus(EntityStatus.INACTIVE);
        entity.setCreatedAt(time.minusDays(1));
        entity.setUpdatedAt(time);

        assertEquals(id, entity.getId());
        assertEquals("Name", entity.getName());
        assertEquals("Desc", entity.getDescription());
        assertEquals(EntityStatus.INACTIVE, entity.getStatus());
    }
}