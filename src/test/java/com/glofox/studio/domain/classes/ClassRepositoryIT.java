package com.glofox.studio.domain.classes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class ClassRepositoryIT {
    @Container
    static MariaDBContainer mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb:11.3.2"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @Autowired
    private ClassRepository classRepository;

    @Test
    public void save_storesClasses() {
        final var clazz = Class.newClass("fitness", LocalDate.of(2024, 1, 1), 20);
        classRepository.save(clazz);

        final var maybeAClass = classRepository.findById(clazz.getId());
        assertTrue(maybeAClass.isPresent());
        final var classFromDb = maybeAClass.get();
        assertEquals(clazz.getId(), classFromDb.getId());
        assertEquals(clazz.getName(), classFromDb.getName());
        assertEquals(clazz.getClassDate(), classFromDb.getClassDate());
        assertEquals(clazz.getCapacity(), classFromDb.getCapacity());
    }

    @Test
    public void save_updatesClass() {
        final var className = "fitness";
        final var initialCapacity = 20;
        final var clazz = Class.newClass(className, LocalDate.of(2024, 1, 1), initialCapacity);
        classRepository.save(clazz);

        var maybeAClass = classRepository.findById(clazz.getId());
        assertTrue(maybeAClass.isPresent());
        final var sameClass = maybeAClass.get();
        assertEquals(initialCapacity, sameClass.getCapacity());

        final var booking = new Booking(UUID.randomUUID(), "John Doe", clazz.getId());
        sameClass.getBookings().add(booking);
        classRepository.save(sameClass);
        maybeAClass = classRepository.findById(clazz.getId());
        assertTrue(maybeAClass.isPresent());
        final var classFromDb = maybeAClass.get();
        assertEquals(1, classFromDb.getBookings().size());
        assertThat(classFromDb.getBookings(), hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("member", equalTo(booking.getMember())),
                hasProperty("classId", equalTo(booking.getClassId()))
        )));
    }

    @Test
    public void save_deletesBookings() {
        final var className = "fitness";
        final var initialCapacity = 20;
        final var clazz = Class.newClass(className, LocalDate.of(2024, 1, 1), initialCapacity);
        final var firstBooking = new Booking(UUID.randomUUID(), "John Doe", clazz.getId());
        final var secondBooking = new Booking(UUID.randomUUID(), "Jane Doe", clazz.getId());
        clazz.getBookings().add(firstBooking);
        clazz.getBookings().add(secondBooking);
        classRepository.save(clazz);

        var maybeAClass = classRepository.findById(clazz.getId());
        assertTrue(maybeAClass.isPresent());
        final var classFromDb = maybeAClass.get();
        assertNotNull(classFromDb.getBookings());
        assertEquals(2, classFromDb.getBookings().size());

        classFromDb.getBookings().clear();
        classRepository.save(classFromDb);
        maybeAClass = classRepository.findById(clazz.getId());
        assertTrue(maybeAClass.isPresent());
        assertEquals(0, maybeAClass.get().getBookings().size());
    }

    @Test
    public void findFirstByClassDate() {
        final var className = "fitness";
        final var initialCapacity = 20;
        final var classDate = LocalDate.of(2024, 1, 1);
        final var clazz = Class.newClass(className, classDate, initialCapacity);
        final var booking = new Booking(UUID.randomUUID(), "John Doe", clazz.getId());
        clazz.getBookings().add(booking);
        classRepository.save(clazz);

        final var maybeAClass = classRepository.findFirstByClassDate(classDate);
        assertTrue(maybeAClass.isPresent());
        assertEquals(clazz.getId(), maybeAClass.get().getId());
    }

}
