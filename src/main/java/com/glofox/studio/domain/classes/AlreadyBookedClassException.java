package com.glofox.studio.domain.classes;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AlreadyBookedClassException extends RuntimeException {

    private final String member;
    private final String className;
    private final LocalDate date;

    public AlreadyBookedClassException(final String member, final String className, final LocalDate date) {
        super(String.format("%1$s already booked class %2$s for %3$tY-%3$tm-%3$td", member, className, date));
        this.member = member;
        this.className = className;
        this.date = date;
    }

}
