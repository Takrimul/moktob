package com.moktob.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
    TUITION_FEE("Tuition Fee"),
    BOOK_FEE("Book Fee"),
    TRANSPORT_FEE("Transport Fee"),
    EXAM_FEE("Exam Fee"),
    OTHER("Other");

    private final String displayName;
}
