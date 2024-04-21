package com.glofox.studio.application;

import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.Class;
import com.glofox.studio.domain.classes.ClassNotFoundException;
import com.glofox.studio.domain.classes.ClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookClassUseCaseTests {
    @Mock
    private ClassRepository classRepository;
    @Mock
    private ClassConverter classConverter;

    private BookClassUseCase useCase;

    @BeforeEach
    public void setup() {
        useCase = new BookClassUseCase(classRepository, classConverter);
    }

    @Test
    public void book_validArgs_createsANewBookingAndStoresIt() {

        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        when(classRepository.findFirstByClassDate(any())).thenReturn(Optional.of(clazz));
        when(classConverter.toDTO(any())).thenReturn(ClassDTO.builder().id(UUID.randomUUID()).build());

        useCase.book("John Doe", classDate);

        verify(classRepository).findFirstByClassDate(classDate);
        verify(classRepository).save(clazz);
        verify(classConverter).toDTO(clazz);
    }

    @Test
    public void book_dateWithNoClasses_throwsException() {

        final var classDate = LocalDate.of(2024, 1, 1);
        when(classRepository.findFirstByClassDate(any())).thenReturn(Optional.empty());

        final var exception = assertThrows(ClassNotFoundException.class, () -> useCase.book("John Doe", classDate));

        assertEquals("class on 2024-01-01 does not exists", exception.getMessage());
        verify(classRepository).findFirstByClassDate(classDate);
        verify(classRepository, never()).save(any());
        verify(classConverter,  never()).toDTO(any());
    }

}
