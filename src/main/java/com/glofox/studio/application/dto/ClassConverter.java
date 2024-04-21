package com.glofox.studio.application.dto;

import com.glofox.studio.domain.classes.Booking;
import com.glofox.studio.domain.classes.Class;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClassConverter {

    public ClassDTO toDTO(Class clazz) {

        final var bookings = Stream.ofNullable(clazz.getBookings())
                .flatMap(Collection::parallelStream)
                .map(Booking::getMember)
                .collect(Collectors.toList());
        return ClassDTO.builder()
                .id(clazz.getId())
                .name(clazz.getName())
                .classDate(clazz.getClassDate())
                .capacity(clazz.getCapacity())
                .bookings(bookings)
                .build();
    }

}
