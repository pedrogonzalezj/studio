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
public class GetClassUseCaseTests {
    @Mock
    private ClassRepository classRepository;
    @Mock
    private ClassConverter classConverter;

    private GetClassUseCase useCase;

    @BeforeEach
    public void setup() {
        useCase = new GetClassUseCase(classRepository, classConverter);
    }

    @Test
    public void getClass_getClassByIdAndConvertItToDTO() {

        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        when(classRepository.findById(any())).thenReturn(Optional.of(clazz));
        when(classConverter.toDTO(any())).thenReturn(ClassDTO.builder().id(UUID.randomUUID()).build());

        useCase.getClass(clazz.getId());

        verify(classRepository).findById(clazz.getId());
        verify(classConverter).toDTO(clazz);
    }

    @Test
    public void getClass_classNotFound_throwsException() {

        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        when(classRepository.findById(any())).thenReturn(Optional.empty());

        final var exception = assertThrows(ClassNotFoundException.class, () -> useCase.getClass(clazz.getId()));

        assertEquals(clazz.getId(), exception.getClassId());
        verify(classRepository).findById(clazz.getId());
        verify(classConverter, never()).toDTO(clazz);
    }

}
