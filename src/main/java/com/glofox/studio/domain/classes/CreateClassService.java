package com.glofox.studio.domain.classes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CreateClassService {

    @Autowired
    private final ClassRepository classRepository;

    public List<Class> createClass(final String name,
                                   final LocalDate startDate,
                                   final LocalDate endDate,
                                   final int capacity) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date must not be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    String.format("Start date %1$tY-%1$tm-%1$td must be before end date %2$tY-%2$tm-%2$td", startDate, endDate)
            );
        }
        final List<Class> classes = new ArrayList<>();
        var classDate = startDate;
        while(classDate.isBefore(endDate) || classDate.isEqual(endDate)) {

            final Optional<Class> maybeDateHasAClass = classRepository.findFirstByClassDate(classDate);
            if (maybeDateHasAClass.isPresent()) {
                log.warn("[Create classes] Failed due to: {} date has already a class of {}", classDate, maybeDateHasAClass.get().getName());

                throw new DateAlreadyTakenException(classDate);
            }
            final var clazz = Class.newClass(name, classDate, capacity);
            classRepository.save(clazz);
            classes.add(clazz);
            classDate = classDate.plusDays(1);
        }
        return classes;
    }

}
