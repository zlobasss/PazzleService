package ru.zlobass.pazzleservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zlobass.pazzleservice.dto.SolvableResponse;
import ru.zlobass.pazzleservice.dto.SolvePuzzleResponse;
import ru.zlobass.pazzleservice.entity.Puzzle;
import ru.zlobass.pazzleservice.entity.PuzzleState;
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
    public ResponseEntity<?> solvePuzzle(@RequestBody Puzzle request) {
        // Преобразуем строку в массив чисел
        int[] puzzleArray = getTilesArray(request.getPuzzle());
        // Проверяем решаема ли задача
        boolean isSolvable = getSolvableStatus(request);
        if (!isSolvable) {
            return new ResponseEntity<>(new SolvePuzzleResponse("Эта комбинация не решаема", false, request.getPuzzle()), HttpStatus.CONFLICT);
        }
        // Получаем решение
        return new ResponseEntity<>(puzzleSolver.solvePuzzle(puzzleArray), HttpStatus.OK);
    }

    @GetMapping("/generate")
    public ResponseEntity<?> generatePuzzle() {
        PuzzleState puzzleState = new PuzzleState();
        return new ResponseEntity<>(new Puzzle(puzzleState.toString()), HttpStatus.OK);
    }

    @PostMapping("/is-solvable")
    public ResponseEntity<?> isSolvablePuzzle(@RequestBody Puzzle request) {
        // Проверяем пазл
        boolean isSolvable = getSolvableStatus(request);
        SolvableResponse solvableResponse = new SolvableResponse(isSolvable, request.getPuzzle());
        return new ResponseEntity<>(solvableResponse, HttpStatus.OK);
    }

    private boolean getSolvableStatus(Puzzle puzzle) {
        int[] puzzleArray = getTilesArray(puzzle.getPuzzle());
        return puzzleSolver.isSolvable(puzzleArray);
    }

    private int[] getTilesArray(String puzzle) {
        String[] tokens = puzzle.split(" ");
        int[] puzzleArray = new int[16];
        for (int i = 0; i < tokens.length; i++) {
            puzzleArray[i] = Integer.parseInt(tokens[i]);
        }
        return puzzleArray;
    }
}
