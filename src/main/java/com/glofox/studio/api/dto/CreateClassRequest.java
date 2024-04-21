package com.glofox.studio.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateClassRequest {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int capacity;

}
