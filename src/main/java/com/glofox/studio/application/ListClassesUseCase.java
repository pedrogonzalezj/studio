package com.glofox.studio.application;

import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListClassesUseCase {

    @Autowired
    private final ClassRepository classRepository;
    @Autowired
    private final ClassConverter classConverter;

    @Transactional(readOnly = true)
    public List<ClassDTO> listAll() {

        return classRepository.findAll()
                .parallelStream()
                .map(classConverter::toDTO)
                .collect(Collectors.toList());
    }

}
