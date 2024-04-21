package com.glofox.studio.application.dto;

import com.glofox.studio.domain.classes.Booking;
import com.glofox.studio.domain.classes.Class;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClassConverterTests {

    private ClassConverter converter;

    @BeforeEach
    public void setup() {
        converter = new ClassConverter();
    }

    @Test
    public void toDTO_convertsClassToDTO() {

        final var className = "fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        final var booking = new Booking(UUID.randomUUID(), "John Doe", clazz.getId());
        clazz.getBookings().add(booking);

        final var dto = converter.toDTO(clazz);

        assertEquals(clazz.getId(), dto.getId());
        assertEquals(className, dto.getName());
        assertEquals(classDate, dto.getClassDate());
        assertEquals(capacity, dto.getCapacity());
        assertNotNull(dto.getBookings());
        assertEquals(1, dto.getBookings().size());
        assertIterableEquals(List.of("John Doe"), dto.getBookings());
    }
}
