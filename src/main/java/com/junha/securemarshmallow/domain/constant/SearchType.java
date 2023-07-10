package com.junha.securemarshmallow.domain.constant;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("본문");

    @Getter private final String description;

    SearchType(String description) {
        this.description = description;
    }

}
