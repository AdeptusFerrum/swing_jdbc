package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityStatusTest {

    @Test
    void testEnumValues() {
        EntityStatus[] values = EntityStatus.values();
        assertEquals(3, values.length);
        assertEquals(EntityStatus.ACTIVE, values[0]);
        assertEquals(EntityStatus.INACTIVE, values[1]);
        assertEquals(EntityStatus.ARCHIVED, values[2]);
    }

    @Test
    void testValueOf() {
        assertEquals(EntityStatus.ACTIVE, EntityStatus.valueOf("ACTIVE"));
        assertEquals(EntityStatus.INACTIVE, EntityStatus.valueOf("INACTIVE"));
        assertEquals(EntityStatus.ARCHIVED, EntityStatus.valueOf("ARCHIVED"));
    }

    @Test
    void testEnumNames() {
        assertEquals("ACTIVE", EntityStatus.ACTIVE.name());
        assertEquals("INACTIVE", EntityStatus.INACTIVE.name());
        assertEquals("ARCHIVED", EntityStatus.ARCHIVED.name());
    }
}
