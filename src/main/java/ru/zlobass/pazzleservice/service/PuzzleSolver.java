package ru.zlobass.pazzleservice.service;

import org.springframework.stereotype.Service;
import ru.zlobass.pazzleservice.entity.PuzzleState;

import java.util.*;

@Service
public class PuzzleSolver {

    public List<String> solvePuzzle(int[] startState) {
        PriorityQueue<PuzzleState> openSet = new PriorityQueue<>(Comparator.comparingInt(PuzzleState::getHeuristic));
        Set<PuzzleState> closedSet = new HashSet<>();

        PuzzleState start = new PuzzleState(startState, 0, null);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            PuzzleState current = openSet.poll();

            if (current.isGoal()) {
                return constructSolution(current);
            }

            closedSet.add(current);

            for (PuzzleState neighbor : current.getNeighbors()) {
                if (!closedSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList(); // Если решения нет
    }

    public boolean isSolvable(int[] startState) {
        List<Integer> tiles = new ArrayList<>();
        for (int i = 0; i < startState.length; i++) {
            tiles.add(startState[i]);
        }
        return PuzzleState.isSolvable(tiles);
    }

    private List<String> constructSolution(PuzzleState state) {
        List<String> solution = new ArrayList<>();
        while (state != null) {
            solution.add(boardToString(state.getBoard()));
            state = state.getPreviousState();
        }
        Collections.reverse(solution); // Решение идёт от последнего состояния к начальному
        return solution;
    }

    private String boardToString(int[] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            sb.append(board[i]);
            if (i < board.length - 1) sb.append(" ");
        }
        return sb.toString();
    }
}
