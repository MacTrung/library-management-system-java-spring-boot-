package com.library.entity;

import lombok.Getter;

@Getter
public enum ReturnStatus {
    NOT_RETURNED("Chưa trả"),
    RETURNED_ON_TIME("Trả đúng hạn"),
    RETURNED_LATE("Trả trễ"),
    DAMAGED("Hỏng");

    private final String displayName;

    ReturnStatus(String displayName) {
        this.displayName = displayName;
    }
}
