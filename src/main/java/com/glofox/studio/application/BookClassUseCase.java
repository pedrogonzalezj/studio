package com.glofox.studio.application;


import com.glofox.studio.application.dto.ClassConverter;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.glofox.studio.domain.classes.ClassNotFoundException;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookClassUseCase {

    @Autowired
    private final ClassRepository classRepository;
    @Autowired
    private final ClassConverter converter;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassDTO book(final String member, final LocalDate classDate) {

        final var clazz = classRepository.findFirstByClassDate(classDate)
                .orElseThrow(() -> new ClassNotFoundException(classDate));
        final var bookingId = clazz.book(member);
        log.info("[Book] class {} booked for {} with bookingId={}", clazz.getName(), member, bookingId);
        classRepository.save(clazz);
        return converter.toDTO(clazz);
    }

}
