package ru.zlobass.pazzleservice.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class PuzzleState {

    private static final int SIZE = 4;
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

    public PuzzleState() {
        this.board = new int[SIZE * SIZE];
        List<Integer> tiles = new ArrayList<>();

        // Заполнение списка цифрами от 0 до 15 (0 - это пустая клетка)
        for (int i = 0; i < SIZE * SIZE; i++) {
            tiles.add(i);
        }

        // Перемешивание фишек до тех пор, пока комбинация не станет решаемой
        do {
            Collections.shuffle(tiles);
        } while (!isSolvable(tiles));

        for (int i = 0; i < SIZE * SIZE; i++) {
            this.board[i] = tiles.get(i);
        }

        this.moves = 0;
        this.previousState = null;
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

    public static boolean isSolvable(List<Integer> tiles) {

        int inversions = countInversions(tiles);
        int emptyRow = findEmptyRow(tiles); // Ряд, в котором находится пустая клетка (нумерация снизу)

        // Если размер поля нечётный, то количество инверсий должно быть чётным
        // Если размер поля чётный, то комбинация решаема, если:
        // - пустая клетка в чётной строке снизу и количество инверсий нечётное
        // - пустая клетка в нечётной строке снизу и количество инверсий чётное
        if (SIZE % 2 == 1) {
            return inversions % 2 == 0;
        } else {
            return (emptyRow % 2 == 1) == (inversions % 2 == 0);
        }
    }

    // Подсчёт количества инверсий в списке
    private static int countInversions(List<Integer> tiles) {
        int inversions = 0;
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = i + 1; j < tiles.size(); j++) {
                if (tiles.get(i) != 0 && tiles.get(j) != 0 && tiles.get(i) > tiles.get(j)) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    // Поиск строки с пустой клеткой (считая снизу)
    private static int findEmptyRow(List<Integer> tiles) {
        int index = tiles.indexOf(0);  // Индекс пустой клетки (0)
        return SIZE - (index / SIZE);  // Ряд пустой клетки (считая снизу)
    }

    private List<Integer> getListBoard() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                list.add(board[i * SIZE + j]);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.append(board[i * SIZE + j]).append(" ");
            }
        }
        return result.toString();
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
