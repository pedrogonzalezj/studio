package com.glofox.studio.domain.classes;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "classes")
public class Class {

    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(name = "class_date", nullable = false)
    private LocalDate classDate;
    @Column(nullable = false)
    private int capacity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name="class_id", referencedColumnName="id")
    private List<Booking> bookings = new ArrayList<>();

    public static Class newClass(
            final String name,
            final LocalDate classDate,
            final int capacity) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Class name must not be null or empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Class capacity must be greater than zero");
        }
        final UUID id = UUID.randomUUID();
        final Class clazz = new Class();
        clazz.setId(id);
        clazz.setName(name);
        clazz.setClassDate(classDate);
        clazz.setCapacity(capacity);
        return clazz;
    }

    public UUID book(final String member) {
        /* // Overbooking check is not needed
        if(bookings.size() >= capacity) {

            throw new IllegalStateException("Class is full");
        } */
        if (member == null || member.isEmpty()) {
            throw new IllegalArgumentException("Member name must not be null or empty");
        }
        final Optional<Booking> maybeAlreadyBookedClass = Stream.ofNullable(bookings)
                .flatMap(Collection::stream)
                .filter(b -> b.getMember().equals(member))
                .findFirst();
        if (maybeAlreadyBookedClass.isPresent()) {
            throw new AlreadyBookedClassException(member, name, classDate);
        }
        final var booking = new Booking(UUID.randomUUID(), member, id);
        bookings.add(booking);
        return booking.getId();
    }

}
