package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new Validator();
    }

    @Test
    void testValidName() {
        assertDoesNotThrow(() -> validator.validateName("Valid Name"));
        assertDoesNotThrow(() -> validator.validateName("Test-Entity"));
        assertDoesNotThrow(() -> validator.validateName("ABC"));
        assertDoesNotThrow(() -> validator.validateName("A".repeat(50)));
    }

    @Test
    void testInvalidName_Null() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateName(null));
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void testInvalidName_Empty() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateName(""));
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void testInvalidName_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateName("AB"));
        assertTrue(exception.getMessage().contains("between 3 and 50"));
    }

    @Test
    void testInvalidName_TooLong() {
        String longName = "A".repeat(51);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateName(longName));
        assertTrue(exception.getMessage().contains("between 3 and 50"));
    }

    @Test
    void testInvalidName_SpecialCharacters() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateName("Name@"));
        assertTrue(exception.getMessage().contains("invalid characters"));
    }

    @Test
    void testValidDescription() {
        assertDoesNotThrow(() -> validator.validateDescription(null));
        assertDoesNotThrow(() -> validator.validateDescription(""));
        assertDoesNotThrow(() -> validator.validateDescription("Valid description"));
        assertDoesNotThrow(() -> validator.validateDescription("A".repeat(255)));
    }

    @Test
    void testInvalidDescription_TooLong() {
        String longDesc = "A".repeat(256);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateDescription(longDesc));
        assertTrue(exception.getMessage().contains("cannot exceed 255"));
    }

    @Test
    void testBoundaryValues() {
        assertDoesNotThrow(() -> validator.validateName("ABC")); // 3 символа
        assertDoesNotThrow(() -> validator.validateName("A".repeat(50))); // 50 символов

        assertDoesNotThrow(() -> validator.validateDescription("A".repeat(255))); // 255 символов

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateName("AB")); // 2 символа

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateName("A".repeat(51))); // 51 символ

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateDescription("A".repeat(256))); // 256 символов
    }
}