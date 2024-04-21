package com.glofox.studio.application;

import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.Class;
import com.glofox.studio.domain.classes.ClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListClassesUseCaseTests {

    @Mock
    private ClassRepository classRepository;
    @Mock
    private ClassConverter classConverter;

    private ListClassesUseCase useCase;

    @BeforeEach
    public void setup() {
        useCase = new ListClassesUseCase(classRepository, classConverter);
    }

    @Test
    public void listAll_getAllClassesAndConvertsThemToDTOs() {

        final var className = "Fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        final var clazz = Class.newClass(className, classDate, capacity);
        when(classRepository.findAll()).thenReturn(List.of(clazz));
        when(classConverter.toDTO(any())).thenReturn(ClassDTO.builder().id(UUID.randomUUID()).build());

        useCase.listAll();

        verify(classRepository).findAll();
        verify(classConverter).toDTO(clazz);
    }

}
