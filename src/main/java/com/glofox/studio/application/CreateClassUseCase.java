package com.glofox.studio.application;



import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.CreateClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateClassUseCase {

    @Autowired
    private final CreateClassService service;
    @Autowired
    private final ClassConverter converter;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<ClassDTO> createClass(final String name,
                                      final LocalDate startDate,
                                      final LocalDate endDate,
                                      final int capacity) {

        final var classes = service.createClass(name, startDate, endDate, capacity);
        log.info("[Create classes] service successfully finished");
        return classes.parallelStream()
                .map(converter::toDTO)
                .collect(Collectors.toList());
    }

}
