package com.glofox.studio.api;

import com.glofox.studio.api.dto.BookRequest;
import com.glofox.studio.domain.classes.AlreadyBookedClassException;
import com.glofox.studio.domain.classes.ClassNotFoundException;
import com.glofox.studio.application.BookClassUseCase;
import com.glofox.studio.application.dto.ClassDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class BookingControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookClassUseCase bookClassUseCase;

    private ClassDTO firstClassDTO;
    private final String member = "John Doe";

    @BeforeEach
    public void setup() {
        final var className = "fitness";
        final var classDate = LocalDate.of(2024, 1, 1);
        final var capacity = 1;
        firstClassDTO = ClassDTO.builder()
                .id(UUID.randomUUID())
                .name(className)
                .classDate(classDate)
                .capacity(capacity)
                .bookings(List.of(member))
                .build();
    }

    @Test
    public void create_createsAClassForEachDay() throws Exception {
        final var selfLink = linkTo(methodOn(BookingController.class).book(firstClassDTO.getClassDate(), new BookRequest())).toString();
        final var getLink = linkTo(methodOn(ClassController.class).get(firstClassDTO.getId())).toString();
        final var requestBody = """
                {
                    "member": "%1$s"
                  }
                """.formatted(member);
        when(bookClassUseCase.book(any(), any())).thenReturn(firstClassDTO);

        mockMvc.perform(post("/classes/{classDate}/bookings", firstClassDTO.getClassDate()).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.id").value(firstClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(firstClassDTO.getName()))
                .andExpect(jsonPath("$.data.classDate").value("2024-01-01"))
                .andExpect(jsonPath("$.data.capacity").value(firstClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.bookings[0]").value(member))
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value(selfLink))
                .andExpect(jsonPath("$.data.links[1].rel").value("get"))
                .andExpect(jsonPath("$.data.links[1].href").value(getLink));
        verify(bookClassUseCase).book(member, firstClassDTO.getClassDate());
    }

    @Test
    public void create_serviceThrowsClassNotFoundException_notFoundErrorResponse() throws Exception {
        final var exception = new ClassNotFoundException(firstClassDTO.getClassDate());
        final var requestBody = """
                {
                    "member": "%1$s"
                  }
                """.formatted(member);
        when(bookClassUseCase.book(any(), any())).thenThrow(exception);

        mockMvc.perform(post("/classes/{classDate}/bookings", firstClassDTO.getClassDate()).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error.message").value(exception.getMessage()))
                .andExpect(jsonPath("$.error.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(bookClassUseCase).book(member, firstClassDTO.getClassDate());
    }


    @Test
    public void create_serviceThrowsAlreadyBookedClassException_conflictErrorResponse() throws Exception {
        final var exception = new AlreadyBookedClassException(member, firstClassDTO.getName(), firstClassDTO.getClassDate());
        final var requestBody = """
                {
                    "member": "%1$s"
                  }
                """.formatted(member);
        when(bookClassUseCase.book(any(), any())).thenThrow(exception);

        mockMvc.perform(post("/classes/{classDate}/bookings", firstClassDTO.getClassDate()).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.error.message").value(exception.getMessage()))
                .andExpect(jsonPath("$.error.type").value("RESOURCE_TAKEN"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(bookClassUseCase).book(member, firstClassDTO.getClassDate());
    }

}
