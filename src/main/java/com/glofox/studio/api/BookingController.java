package com.glofox.studio.api;


import com.glofox.studio.api.dto.BookRequest;
import com.glofox.studio.application.BookClassUseCase;
import com.glofox.studio.application.dto.ClassDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    private final BookClassUseCase bookClassUseCase;

    @PostMapping("/classes/{classDate}/bookings")
    public GenericResponse<EntityModel<ClassDTO>> book(
            @PathVariable LocalDate classDate,
            @RequestBody final BookRequest request) {
        log.info("[Book] Received request to book a new class");
        final var dto = bookClassUseCase.book(request.getMember(), classDate);
        final var self = linkTo(methodOn(BookingController.class).book(classDate, request)).withSelfRel();
        final var model = EntityModel.of(
                dto,
                self,
                linkTo(methodOn(ClassController.class).get(dto.getId())).withRel("get")
        );
        return new GenericResponse<>(null, model);
    }

}
