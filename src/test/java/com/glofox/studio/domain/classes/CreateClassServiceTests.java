package com.glofox.studio.domain.classes;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
public class CreateClassServiceTests {

    @Mock
    private ClassRepository classRepository;
    private CreateClassService service;

    @BeforeEach
    public void setup() {
        service = new CreateClassService(classRepository);
    }

    @Test
    public void createClass_validArgs_createsOneClassForEachDate() {
        final var name = "Fitness";
        final var startDate = LocalDate.of(2024, 1, 1);
        final var endDate = LocalDate.of(2024, 1, 2);
        final var capacity = 10;
        final var firstClass = Class.newClass(name, startDate, capacity);
        final var secondClass = Class.newClass(name, endDate, capacity);
        when(classRepository.findFirstByClassDate(any())).thenReturn(Optional.empty());
        when(classRepository.save(any())).thenReturn(firstClass, secondClass);

        service.createClass(name, startDate, endDate, capacity);

        verify(classRepository).findFirstByClassDate(startDate);
        verify(classRepository).findFirstByClassDate(endDate);
        verify(classRepository, times(2)).save(
                argThat(w -> w.getId() != null &&
                        (w.getClassDate().equals(startDate) || w.getClassDate().equals(endDate)) &&
                        w.getName().equals(name) &&
                        w.getCapacity() == capacity
                )
        );
    }

    @Test
    public void createClass_endDateBeforeStartDate_throwsException() {
        final var name = "Fitness";
        final var startDate = LocalDate.of(2024, 1, 2);
        final var endDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;

        final var exception = assertThrows(IllegalArgumentException.class, () -> service.createClass(name, startDate, endDate, capacity));

        assertEquals("Start date 2024-01-02 must be before end date 2024-01-01", exception.getMessage());
        verify(classRepository, never()).findFirstByClassDate(any());
        verify(classRepository, never()).save(any());
    }

    @Test
    public void createClass_dateAlreadyContainsAClass_throwsException() {
        final var name = "Fitness";
        final var startDate = LocalDate.of(2024, 1, 2);
        final var endDate = LocalDate.of(2024, 1, 3);
        final var capacity = 10;
        final var firstClass = Class.newClass(name, startDate, capacity);
        when(classRepository.findFirstByClassDate(startDate)).thenReturn(Optional.of(firstClass));

        final var exception = assertThrows(DateAlreadyTakenException.class, () -> service.createClass(name, startDate, endDate, capacity));

        assertEquals(startDate, exception.getDate());
        verify(classRepository).findFirstByClassDate(startDate);
        verify(classRepository, never()).save(any());
    }

    @Test
    public void createClass_startDateIsNull_throwsException() {
        final var name = "Fitness";
        final var endDate = LocalDate.of(2024, 1, 1);
        final var capacity = 10;

        final var exception = assertThrows(IllegalArgumentException.class, () -> service.createClass(name, null, endDate, capacity));

        assertEquals("Start date must not be null", exception.getMessage());
        verify(classRepository, never()).findFirstByClassDate(any());
        verify(classRepository, never()).save(any());
    }

    @Test
    public void createClass_endDateIsNull_throwsException() {
        final var name = "Fitness";
        final var startDate = LocalDate.of(2024, 1, 2);
        final var capacity = 10;

        final var exception = assertThrows(IllegalArgumentException.class, () -> service.createClass(name, startDate, null, capacity));

        assertEquals("End date must not be null", exception.getMessage());
        verify(classRepository, never()).findFirstByClassDate(any());
        verify(classRepository, never()).save(any());
    }

}