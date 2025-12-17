package org.example.util;

public class Validator {

    public void validateName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters");
        }

        if (!name.matches("^[a-zA-Z0-9\\s\\-._]*$")) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }
    }

    public void validateDescription(String description) throws IllegalArgumentException {
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Description cannot exceed 255 characters");
        }
    }
}