package ru.zlobass.pazzleservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zlobass.pazzleservice.entity.Puzzle;
import ru.zlobass.pazzleservice.service.PuzzleSolver;

import java.util.List;

@RestController
@RequestMapping("/api/puzzle")
public class PuzzleController {

    private final PuzzleSolver puzzleSolver;

    public PuzzleController(PuzzleSolver puzzleSolver) {
        this.puzzleSolver = puzzleSolver;
    }

    @PostMapping("/solve")
    public List<String> solvePuzzle(@RequestBody Puzzle request) {
        // Преобразуем строку в массив чисел
        String[] tokens = request.getPuzzle().split(" ");
        int[] puzzleArray = new int[16];
        for (int i = 0; i < tokens.length; i++) {
            puzzleArray[i] = Integer.parseInt(tokens[i]);
        }

        // Получаем решение
        return puzzleSolver.solvePuzzle(puzzleArray);
    }
}
