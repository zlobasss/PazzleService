package ru.zlobass.pazzleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolvePuzzleResponse {
    private String message;
    private boolean isSolvable;
    private String puzzle;
}
