package com.library.entity;

public enum BookCondition {
    NEW("Mới"), OLD("Cũ"), DAMAGED("Hỏng");

    private final String displayName;

    BookCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
