package hu.gerab.twentyQuestions;

import hu.gerab.util.ConsoleColors;
import java.util.ArrayList;
import java.util.List;

public class Progress {

    private final int lieAt;
    private boolean lied = false;
    private final int number;
    private int questionCount = 0;
    private boolean concluded = false;

    private final List<Integer> available = new ArrayList<>(
            List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
    private final List<Integer> eliminatedOnce = new ArrayList<>();
    private final List<Integer> eliminatedTwice = new ArrayList<>();

    public Progress(int lieAt, int number) {
        this.lieAt = lieAt;
        this.number = number;
    }

    public boolean registerQuestion() {
        if (concluded) {
            return false;
        }
        final boolean isLie = lieAt == ++questionCount;
        if (isLie) {
            lied = true;
        }
        return isLie;
    }

    public void eliminateExcludedNumbers(List<Integer> numbers, boolean excludeNumbers) {
        if (concluded) {
            return;
        }
        List<Integer> doubleEliminate;
        List<Integer> eliminate;
        if (excludeNumbers) {
            doubleEliminate = eliminatedOnce.stream().filter(n -> !numbers.contains(n)).toList();
            eliminate = available.stream().filter(n -> !numbers.contains(n)).toList();
        } else {
            doubleEliminate = eliminatedOnce.stream().filter(numbers::contains).toList();
            eliminate = available.stream().filter(numbers::contains).toList();
        }
        eliminatedOnce.removeAll(doubleEliminate);
        eliminatedTwice.addAll(doubleEliminate);
        available.removeAll(eliminate);
        eliminatedOnce.addAll(eliminate);
        String sb = "\t" + this
                + ", new{once=" + eliminate.stream().toList()
                + ", twice=" + doubleEliminate.stream().toList()
                + "}";
        System.out.println(sb);
        if (isDecided()) {
            final ArrayList<Integer> possibilities = getAllPossible();
            concluded = true;
            if (possibilities.get(0).equals(number)) {
                System.out.println("Terminal condition reached with correct number=" + number +
                        ", in steps=" + questionCount);
            } else {
                System.out.println("Terminal condition reached with incorrect number=" + numbers.get(0) +
                        ", in steps=" + questionCount);
            }
        }
    }

    public String toString() {
        return "number=" + number
                + ", lied=" + (lied ? "yes" : " no")
                + ", available=" + String.format("%31s", available.stream().toList())
                + ", eliminated{once=" + String.format("%36s", eliminatedOnce.stream().toList())
                + ", twice=" + String.format("%53s", eliminatedTwice.stream().toList())
                + "}";
    }


    public boolean isConcluded() {
        return concluded;
    }

    public boolean isConcludedAndOk() {
        return concluded && isDecided();
    }

    public boolean isDecided() {
        return available.size() + eliminatedOnce.size() == 1;
    }

    public void printState() {
        final ArrayList<Integer> possibilities = getAllPossible();
        if (possibilities.size() == 1) {
            Integer result = possibilities.get(0);
            if (result.equals(number)) {
                System.out.println("Found expected expected result=" + result +
                        ", in steps=" + questionCount);
            } else {
                System.out.println(ConsoleColors.RED +
                        "Eliminated everything but =" + result + ", but the actual number was=" + number
                        + ", in steps=" + questionCount
                        + ConsoleColors.RESET);
            }
        } else {
            System.out.println(
                    ConsoleColors.RED +
                            "Inconclusive results. Possible numbers=" + possibilities + ", the actual number was="
                            + number + ", in steps=" + questionCount
                            + ConsoleColors.RESET);
        }
    }

    public Integer getIfSingleAvailable() {
        if (available.size() != 1) {
            throw new IllegalStateException();
        }
        return available.get(0);
    }

    public ArrayList<Integer> getAllPossible() {
        ArrayList<Integer> possibilities = new ArrayList<>();
        possibilities.addAll(available);
        possibilities.addAll(eliminatedOnce);
        return possibilities;
    }
}
