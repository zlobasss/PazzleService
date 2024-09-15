package ru.zlobass.pazzleservice.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PuzzleState {

    private int[] board;
    private int zeroIndex; // Индекс пустой плитки (0)
    private int moves;
    private PuzzleState previousState;

    private static final Map<String, Integer> heuristicCache = new HashMap<>(); // Кеш для эвристик

    public PuzzleState(int[] board, int moves, PuzzleState previousState) {
        this.board = Arrays.copyOf(board, board.length);
        this.moves = moves;
        this.previousState = previousState;
        this.zeroIndex = findZeroIndex(board);
    }

    public int[] getBoard() {
        return board;
    }

    public int getMoves() {
        return moves;
    }

    public PuzzleState getPreviousState() {
        return previousState;
    }

    public int getZeroIndex() {
        return zeroIndex;
    }

    private int findZeroIndex(int[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    // Эвристика с кешированием
    public int getHeuristic() {
        String boardKey = Arrays.toString(board); // Преобразуем доску в строку для кеша
        return heuristicCache.computeIfAbsent(boardKey, key -> manhattanDistance() + linearConflict());
    }

    // Манхэттенское расстояние
    public int manhattanDistance() {
        int distance = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) continue;
            int targetX = (board[i] - 1) / 4;
            int targetY = (board[i] - 1) % 4;
            int currentX = i / 4;
            int currentY = i % 4;
            distance += Math.abs(targetX - currentX) + Math.abs(targetY - currentY);
        }
        return distance;
    }

    // Линейные конфликты
    public int linearConflict() {
        int conflict = 0;
        // Проверяем строки на конфликты
        for (int row = 0; row < 4; row++) {
            conflict += countConflicts(row, true);
        }
        // Проверяем столбцы на конфликты
        for (int col = 0; col < 4; col++) {
            conflict += countConflicts(col, false);
        }
        return conflict;
    }

    // Подсчет конфликтов в строках или столбцах
    private int countConflicts(int index, boolean isRow) {
        int conflict = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                int pos1 = isRow ? index * 4 + i : i * 4 + index;
                int pos2 = isRow ? index * 4 + j : j * 4 + index;

                if (board[pos1] == 0 || board[pos2] == 0) continue;

                int target1 = board[pos1] - 1;
                int target2 = board[pos2] - 1;

                if (isRow && target1 / 4 == target2 / 4 && target1 > target2) {
                    conflict += 2;
                } else if (!isRow && target1 % 4 == target2 % 4 && target1 > target2) {
                    conflict += 2;
                }
            }
        }
        return conflict;
    }

    // Проверка на решение
    public boolean isGoal() {
        for (int i = 0; i < 15; i++) {
            if (board[i] != i + 1) {
                return false;
            }
        }
        return board[15] == 0;
    }

    public PuzzleState[] getNeighbors() {
        // Возможные перемещения пустой плитки: вверх, вниз, влево, вправо
        int[][] shifts = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        PuzzleState[] neighbors = new PuzzleState[4];
        int zeroX = zeroIndex / 4;
        int zeroY = zeroIndex % 4;
        int index = 0;

        for (int[] shift : shifts) {
            int newX = zeroX + shift[0];
            int newY = zeroY + shift[1];
            if (newX >= 0 && newX < 4 && newY >= 0 && newY < 4) {
                int[] newBoard = Arrays.copyOf(board, board.length);
                newBoard[zeroIndex] = newBoard[newX * 4 + newY];
                newBoard[newX * 4 + newY] = 0;
                neighbors[index++] = new PuzzleState(newBoard, moves + 1, this);
            }
        }
        return Arrays.copyOf(neighbors, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PuzzleState)) return false;
        return Arrays.equals(board, ((PuzzleState) obj).board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
