package com.example.lal.model.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    //ENUM('Male', 'Female', 'Helicopter')
    male("male"),
    female("female"),
    helicopter("helicopter");
    private final String value;

    private Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}