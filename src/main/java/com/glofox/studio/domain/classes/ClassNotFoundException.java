package com.glofox.studio.domain.classes;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class ClassNotFoundException extends RuntimeException {
    private final UUID classId;
    private final LocalDate localDate;

    public ClassNotFoundException(final UUID classId) {
        super(String.format("class with id=%s does not exists", classId));
        this.classId = classId;
        this.localDate = null;

    }

    public ClassNotFoundException(final LocalDate localDate) {
        super(String.format("class on %1$tY-%1$tm-%1$td does not exists", localDate));
        this.classId = null;
        this.localDate = localDate;

    }

    public String getClassIdentifier() {
        if (classId != null) {
            return classId.toString();
        }
        if (localDate != null) {
            return String.format("%1$tY-%1$tm-%1$td", localDate);
        }
        return null;
    }
}
