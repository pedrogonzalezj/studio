package com.glofox.studio.application;

import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.Class;
import com.glofox.studio.domain.classes.CreateClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateClassUseCaseTests {

    @Mock
    private CreateClassService createClassService;
    @Mock
    private ClassConverter classConverter;

    private CreateClassUseCase useCase;

    @BeforeEach
    public void setup() {
        useCase = new CreateClassUseCase(createClassService, classConverter);
    }

    @Test
    public void createClass_createsNewClassesAndConvertsThemToDTO() {

        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        when(createClassService.createClass(any(), any(), any(), anyInt())).thenReturn(List.of(clazz));
        when(classConverter.toDTO(any())).thenReturn(ClassDTO.builder().id(UUID.randomUUID()).build());

        useCase.createClass(className, classDate, classDate, capacity);

        verify(createClassService).createClass(className, classDate, classDate, capacity);
        verify(classConverter).toDTO(clazz);
    }
}
