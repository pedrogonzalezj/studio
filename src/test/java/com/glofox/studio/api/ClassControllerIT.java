package com.glofox.studio.api;

import com.glofox.studio.api.dto.BookRequest;
import com.glofox.studio.api.dto.CreateClassRequest;
import com.glofox.studio.application.CreateClassUseCase;
import com.glofox.studio.application.GetClassUseCase;
import com.glofox.studio.application.ListClassesUseCase;
import com.glofox.studio.application.dto.ClassDTO;
import com.glofox.studio.domain.classes.DateAlreadyTakenException;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class ClassControllerIT {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateClassUseCase createClassUseCase;
    @MockBean
    private ListClassesUseCase listClassesUseCase;
    @MockBean
    private GetClassUseCase getClassUseCase;

    private ClassDTO firstClassDTO;
    private ClassDTO secondClassDTO;

    @BeforeEach
    public void setup() {
        final var className = "fitness";
        final var classDate = LocalDate.of(2024, 1, 21);
        final var capacity = 1;
        firstClassDTO = ClassDTO.builder()
                .id(UUID.randomUUID())
                .name(className)
                .classDate(classDate)
                .capacity(capacity)
                .bookings(List.of("John Doe"))
                .build();
        secondClassDTO = ClassDTO.builder()
                .id(UUID.randomUUID())
                .name(className)
                .classDate(classDate.plusDays(1))
                .capacity(capacity)
                .bookings(List.of("Jane Doe"))
                .build();
    }

    @Test
    public void get_returnClassInfo() throws Exception {

        final var selfLink = linkTo(methodOn(ClassController.class).get(firstClassDTO.getId())).toString();
        final var listAllLink = linkTo(methodOn(ClassController.class).listAll()).toString();
        final var bookLink = linkTo(methodOn(BookingController.class).book(firstClassDTO.getClassDate(), new BookRequest())).toString();

        when(getClassUseCase.getClass(any())).thenReturn(firstClassDTO);

        mockMvc.perform(get("/classes/{id}", firstClassDTO.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.id").value(firstClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(firstClassDTO.getName()))
                .andExpect(jsonPath("$.data.classDate").value(firstClassDTO.getClassDate().format(formatter)))
                .andExpect(jsonPath("$.data.capacity").value(firstClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.bookings[0]").value("John Doe"))
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value(selfLink))
                .andExpect(jsonPath("$.data.links[1].rel").value("listAll"))
                .andExpect(jsonPath("$.data.links[1].href").value(listAllLink))
                .andExpect(jsonPath("$.data.links[2].rel").value("book"))
                .andExpect(jsonPath("$.data.links[2].href").isNotEmpty());
        verify(getClassUseCase).getClass(firstClassDTO.getId());
    }

    @Test
    public void listAll_returnsAllClassesInfo() throws Exception {
        final var selfLink = linkTo(methodOn(ClassController.class).listAll()).toString();
        final var firstLink = linkTo(methodOn(ClassController.class).get(firstClassDTO.getId())).toString();
        final var secondLink = linkTo(methodOn(ClassController.class).get(secondClassDTO.getId())).toString();
        when(listClassesUseCase.listAll()).thenReturn(List.of(firstClassDTO, secondClassDTO));

        mockMvc.perform(get("/classes").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value(selfLink))
                .andExpect(jsonPath("$.data.content[0].id").value(firstClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.content[0].name").value(firstClassDTO.getName()))
                .andExpect(jsonPath("$.data.content[0].classDate").value(firstClassDTO.getClassDate().format(formatter)))
                .andExpect(jsonPath("$.data.content[0].capacity").value(firstClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.content[0].bookings[0]").value("John Doe"))
                .andExpect(jsonPath("$.data.content[0].links[0].rel").value("get"))
                .andExpect(jsonPath("$.data.content[0].links[0].href").value(linkTo(methodOn(ClassController.class).get(firstClassDTO.getId())).toString()))
                .andExpect(jsonPath("$.data.content[0].links[1].rel").value("book"))
                .andExpect(jsonPath("$.data.content[0].links[1].href").isNotEmpty())
                .andExpect(jsonPath("$.data.content[1].id").value(secondClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.content[1].name").value(secondClassDTO.getName()))
                .andExpect(jsonPath("$.data.content[1].classDate").value(secondClassDTO.getClassDate().format(formatter)))
                .andExpect(jsonPath("$.data.content[1].capacity").value(secondClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.content[1].bookings[0]").value("Jane Doe"))
                .andExpect(jsonPath("$.data.content[1].links[0].rel").value("get"))
                .andExpect(jsonPath("$.data.content[1].links[0].href").value(linkTo(methodOn(ClassController.class).get(secondClassDTO.getId())).toString()))
                .andExpect(jsonPath("$.data.content[1].links[1].rel").value("book"))
                .andExpect(jsonPath("$.data.content[1].links[1].href").isNotEmpty());
        verify(listClassesUseCase).listAll();
    }

    @Test
    public void create_createsAClassForEachDay() throws Exception {
        final var request = new CreateClassRequest();
        request.setName(firstClassDTO.getName());
        request.setStartDate(firstClassDTO.getClassDate());
        request.setEndDate(secondClassDTO.getClassDate());
        request.setCapacity(firstClassDTO.getCapacity());
        final var selfLink = linkTo(methodOn(ClassController.class).create(request)).toString();
        final var requestBody = """
                {
                    "name": "%1$s",
                    "startDate": "%2$tY-%2$tm-%2$td",
                    "endDate": "%3$tY-%3$tm-%3$td",
                    "capacity": %4$s
                  }
                """.formatted(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
        when(createClassUseCase.createClass(any(), any(), any(), anyInt())).thenReturn(List.of(firstClassDTO, secondClassDTO));

        mockMvc.perform(post("/classes").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value(selfLink))
                .andExpect(jsonPath("$.data.content[0].id").value(firstClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.content[0].name").value(firstClassDTO.getName()))
                .andExpect(jsonPath("$.data.content[0].classDate").value(firstClassDTO.getClassDate().format(formatter)))
                .andExpect(jsonPath("$.data.content[0].capacity").value(firstClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.content[0].bookings[0]").value("John Doe"))
                .andExpect(jsonPath("$.data.content[0].links[0].rel").value("get"))
                .andExpect(jsonPath("$.data.content[0].links[0].href").value(linkTo(methodOn(ClassController.class).get(firstClassDTO.getId())).toString()))
                .andExpect(jsonPath("$.data.content[0].links[1].rel").value("book"))
                .andExpect(jsonPath("$.data.content[0].links[1].href").isNotEmpty())
                .andExpect(jsonPath("$.data.content[1].id").value(secondClassDTO.getId().toString()))
                .andExpect(jsonPath("$.data.content[1].name").value(secondClassDTO.getName()))
                .andExpect(jsonPath("$.data.content[1].classDate").value(secondClassDTO.getClassDate().format(formatter)))
                .andExpect(jsonPath("$.data.content[1].capacity").value(secondClassDTO.getCapacity()))
                .andExpect(jsonPath("$.data.content[1].bookings[0]").value("Jane Doe"))
                .andExpect(jsonPath("$.data.content[1].links[0].rel").value("get"))
                .andExpect(jsonPath("$.data.content[1].links[0].href").value(linkTo(methodOn(ClassController.class).get(secondClassDTO.getId())).toString()))
                .andExpect(jsonPath("$.data.content[1].links[1].rel").value("book"))
                .andExpect(jsonPath("$.data.content[1].links[1].href").isNotEmpty());
        verify(createClassUseCase).createClass(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
    }

    @Test
    public void create_serviceThrowsIllegalArgumentException_badRequestResponse() throws Exception {
        final var request = new CreateClassRequest();
        request.setName(firstClassDTO.getName());
        request.setStartDate(firstClassDTO.getClassDate());
        request.setEndDate(secondClassDTO.getClassDate());
        request.setCapacity(firstClassDTO.getCapacity());
        final var exception = new IllegalArgumentException("Start date must not be null");
        final var requestBody = """
                {
                    "name": "%1$s",
                    "startDate": "%2$tY-%2$tm-%2$td",
                    "endDate": "%3$tY-%3$tm-%3$td",
                    "capacity": %4$s
                  }
                """.formatted(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
        when(createClassUseCase.createClass(any(), any(), any(), anyInt())).thenThrow(exception);

        mockMvc.perform(post("/classes").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error.message").value(exception.getMessage()))
                .andExpect(jsonPath("$.error.type").value("BAD_PARAMS"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(createClassUseCase).createClass(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
    }

    @Test
    public void create_serviceThrowsDateAlreadyTakenException_conflictResponse() throws Exception {
        final var request = new CreateClassRequest();
        request.setName(firstClassDTO.getName());
        request.setStartDate(firstClassDTO.getClassDate());
        request.setEndDate(secondClassDTO.getClassDate());
        request.setCapacity(firstClassDTO.getCapacity());
        final var exception = new DateAlreadyTakenException(firstClassDTO.getClassDate());
        final var requestBody = """
                {
                    "name": "%1$s",
                    "startDate": "%2$tY-%2$tm-%2$td",
                    "endDate": "%3$tY-%3$tm-%3$td",
                    "capacity": %4$s
                  }
                """.formatted(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
        when(createClassUseCase.createClass(any(), any(), any(), anyInt())).thenThrow(exception);
        mockMvc.perform(post("/classes").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.error.message").value(exception.getMessage()))
                .andExpect(jsonPath("$.error.type").value("RESOURCE_TAKEN"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(createClassUseCase).createClass(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
    }

    @Test
    public void create_serviceThrowsRuntimeException_internalServerErrorResponse() throws Exception {
        final var request = new CreateClassRequest();
        request.setName(firstClassDTO.getName());
        request.setStartDate(firstClassDTO.getClassDate());
        request.setEndDate(secondClassDTO.getClassDate());
        request.setCapacity(firstClassDTO.getCapacity());
        final var exception = new RuntimeException("something failed in the db");
        final var requestBody = """
                {
                    "name": "%1$s",
                    "startDate": "%2$tY-%2$tm-%2$td",
                    "endDate": "%3$tY-%3$tm-%3$td",
                    "capacity": %4$s
                  }
                """.formatted(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
        when(createClassUseCase.createClass(any(), any(), any(), anyInt())).thenThrow(exception);

        mockMvc.perform(post("/classes").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.error.message").value(exception.getMessage()))
                .andExpect(jsonPath("$.error.type").value("SERVICE_ERROR"))
                .andExpect(jsonPath("$.data").isEmpty());
        verify(createClassUseCase).createClass(firstClassDTO.getName(), firstClassDTO.getClassDate(), secondClassDTO.getClassDate(), firstClassDTO.getCapacity());
    }

}
