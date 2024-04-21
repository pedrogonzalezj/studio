package com.glofox.studio.domain.classes;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DateAlreadyTakenException extends RuntimeException {

    private final LocalDate date;

    public DateAlreadyTakenException(final LocalDate date) {
        super(String.format("%1$tY-%1$tm-%1$td date already has classes", date));
        this.date = date;
    }

}
