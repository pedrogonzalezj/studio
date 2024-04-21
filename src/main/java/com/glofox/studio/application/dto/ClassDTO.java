package com.glofox.studio.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class ClassDTO {

    private UUID id;
    private String name;
    private LocalDate classDate;
    private int capacity;
    private List<String> bookings;

}
