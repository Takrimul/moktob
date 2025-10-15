package com.moktob.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayerType {
    STUDENT("Student"),
    TEACHER("Teacher"),
    PARENT("Parent"),
    OTHER("Other");

    private final String displayName;
}
