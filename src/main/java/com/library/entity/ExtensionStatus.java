package com.library.entity;

public enum ExtensionStatus {
    CREATED("Đã tạo"),
    PROCESSING("Đang xử lý"),
    CANCELLED("Đã hủy"),
    APPROVED("Chấp nhận"),
    REJECTED("Từ chối");

    private final String displayName;

    ExtensionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
