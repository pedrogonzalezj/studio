package com.glofox.studio.api;


import com.glofox.studio.api.dto.BookRequest;
import com.glofox.studio.api.dto.CreateClassRequest;
import com.glofox.studio.application.CreateClassUseCase;
import com.glofox.studio.application.GetClassUseCase;
import com.glofox.studio.application.ListClassesUseCase;
import com.glofox.studio.application.dto.ClassDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClassController {

    @Autowired
    private final CreateClassUseCase createClassUseCase;
    @Autowired
    private final ListClassesUseCase listClassesUseCase;
    @Autowired
    private final GetClassUseCase getClassUseCase;

    @GetMapping("/classes/{id}")
    public GenericResponse<EntityModel<ClassDTO>> get(@PathVariable UUID id) {
        log.info("Received request to get class {}.", id);
        final var clazz = getClassUseCase.getClass(id);
        final var self = linkTo(methodOn(ClassController.class).get(id)).withSelfRel();
        final var model = EntityModel.of(
                clazz,
                self,
                linkTo(methodOn(ClassController.class).listAll()).withRel("listAll"),
                linkTo(methodOn(BookingController.class).book(clazz.getClassDate(), new BookRequest())).withRel("book")
        );
        return new GenericResponse<>(null, model);
    }

    @GetMapping("/classes")
    public GenericResponse<CollectionModel<EntityModel<ClassDTO>>> listAll() {
        log.info("Received request to list all available class.");
        final var dtos = listClassesUseCase.listAll();
        final var self = linkTo(methodOn(ClassController.class).listAll()).withSelfRel();
        final List<EntityModel<ClassDTO>> models = dtos.stream()
                .map(clazz -> EntityModel.of(clazz,
                                linkTo(methodOn(ClassController.class).get(clazz.getId())).withRel("get"),
                                linkTo(methodOn(BookingController.class).book(clazz.getClassDate(), new BookRequest())).withRel("book")
                        )
                ).toList();
        return new GenericResponse<>(null, CollectionModel.of(models, self));
    }

    @PostMapping("/classes")
    public GenericResponse<CollectionModel<EntityModel<ClassDTO>>> create(@RequestBody CreateClassRequest request) {
        log.info("[Create classes] Received request to create new class. Request:{}", request);
        final var dtos = createClassUseCase.createClass(
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCapacity()
        );
        final var self = linkTo(methodOn(ClassController.class).create(request)).withSelfRel();
        final List<EntityModel<ClassDTO>> models = dtos.stream()
                .map(clazz -> EntityModel.of(clazz,
                        linkTo(methodOn(ClassController.class).get(clazz.getId())).withRel("get"),
                        linkTo(methodOn(BookingController.class).book(clazz.getClassDate(), new BookRequest())).withRel("book")
                )
                ).toList();
        return new GenericResponse<>(null, CollectionModel.of(models, self));
    }

}
