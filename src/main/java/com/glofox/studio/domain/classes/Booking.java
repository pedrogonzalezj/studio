package com.glofox.studio.domain.classes;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String member;

    @Column(name="class_id")
    private UUID classId;

}
