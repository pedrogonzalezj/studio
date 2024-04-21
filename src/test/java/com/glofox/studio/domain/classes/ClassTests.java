package com.glofox.studio.domain.classes;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class ClassTests {

    @ParameterizedTest
    @ValueSource(ints = { -2, -1, 0 })
    public void newClass_wrongCapacity_createAClass(final int c) {
        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);

        final var exception = assertThrows(IllegalArgumentException.class, () -> Class.newClass(className, classDate, c));

        assertEquals("Class capacity must be greater than zero", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void newClass_wrongClassName_createAClass(final String n) {
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;

        final var exception = assertThrows(IllegalArgumentException.class, () -> Class.newClass(n, classDate, capacity));

        assertEquals("Class name must not be null or empty", exception.getMessage());
    }

    @Test
    public void newClass_validArgs_createAClass() {
        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;

        final var clazz = Class.newClass(className, classDate, capacity);

        assertNotNull(clazz.getId());
        assertEquals(className, clazz.getName());
        assertEquals(classDate, clazz.getClassDate());
        assertEquals(capacity, clazz.getCapacity());
        assertNotNull(clazz.getBookings());
        assertEquals(0, clazz.getBookings().size());
    }

    @Test
    public void book_validArgs_addsNewBooking() {
        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;
        final var clazz = Class.newClass(className, classDate, capacity);
        final var booking = new Booking(UUID.randomUUID(), "Jane Doe", clazz.getId());
        clazz.getBookings().add(booking);

        final var bookingId = clazz.book("John Doe");

        assertNotNull(bookingId);
        assertEquals(2, clazz.getBookings().size());
        assertThat(clazz.getBookings(), hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("member", equalTo("John Doe")),
                hasProperty("classId", equalTo(clazz.getId()))
        )));
    }

    @Test
    public void book_alreadyBookedMember_throwsException() {
        final var member = "John Doe";
        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;
        final var clazz = Class.newClass(className, classDate, capacity);
        final var booking = new Booking(UUID.randomUUID(), member, clazz.getId());
        clazz.getBookings().add(booking);

        final var exception = assertThrows(AlreadyBookedClassException.class, () -> clazz.book(member));

        assertEquals(member, exception.getMember());
        assertEquals(className, exception.getClassName());
        assertEquals(classDate, exception.getDate());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void book_nullMemberName_throwsException(final String m) {
        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;
        final var clazz = Class.newClass(className, classDate, capacity);

        final var exception = assertThrows(IllegalArgumentException.class, () -> clazz.book(m));

        assertEquals("Member name must not be null or empty", exception.getMessage());
    }

}
