package hu.gerab.twentyQuestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {

    private int number = 0;
    private final List<Integer> numbers;
    private Boolean lied;
    private Boolean answer;

    private Question followUpTrue;
    private Question followUpFalse;

    public Question(List<Integer> numbers) {
        this.numbers = new ArrayList<>(numbers);
    }

    public Question(int... numbers) {
        this(Arrays.stream(numbers).boxed().toList());
    }

    public Question followUpTrue(Question followUpTrue) {
        this.followUpTrue = followUpTrue;
        return followUpTrue;
    }

    public Question followUpFalse(Question followUpFalse) {
        this.followUpFalse = followUpFalse;
        return followUpFalse;
    }

    public boolean ask(int number, Progress p) {
        final boolean answer = contains(number, false, p.registerQuestion());
        System.out.print(this);
        p.eliminateExcludedNumbers(numbers, answer);
        Question followUp = getFollowUp();
        if (followUp != null && followUp != this && !p.isConcluded()) {
            followUp.ask(number, p);
        }
        return answer;
    }

    public boolean contains(int number, boolean canLie, boolean forceLie) {
        if (number == 0) {
            throw new IllegalArgumentException();
        }
        if (this.number != 0) {
            if (this.number == number) {
                return answer;
            } else {
                throw new IllegalArgumentException();
            }
        }
        this.number = number;
        final boolean contains = numbers.contains(number);
        lied = forceLie | (canLie & Math.random() % 2 == 0);
        answer = lied ^ contains;
        return answer;
    }

    public Question getFollowUp() {
        if (answer == null) {
            throw new IllegalStateException();
        }
        return answer ? followUpTrue : followUpFalse;
    }

    public Question getFollowUpTrue() {
        return followUpTrue;
    }

    public Question getFollowUpFalse() {
        return followUpFalse;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    @Override
    public String toString() {
        return "{" + String.format("%30s", numbers.stream().toList())
                + ", lied=" + (lied == null ? " - " : (lied ? "yes" : " no"))
                + ", answer=" + (answer == null ? " - " : (answer ? "yes" : " no"))
                + '}';
    }
}
