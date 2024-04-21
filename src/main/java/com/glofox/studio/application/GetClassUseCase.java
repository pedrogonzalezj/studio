package com.glofox.studio.application;

import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.ClassNotFoundException;
import com.glofox.studio.domain.classes.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetClassUseCase {

    @Autowired
    private final ClassRepository classRepository;
    @Autowired
    private final ClassConverter classConverter;

    @Transactional(readOnly = true)
    public ClassDTO getClass(final UUID id) {
        final var clazz = classRepository.findById(id).orElseThrow(() -> new ClassNotFoundException(id));
        return classConverter.toDTO(clazz);
    }

}
