package ru.zlobass.pazzleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolvableResponse {
    private boolean solved;
    private String puzzle;
}
