package hu.gerab.twentyQuestions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        List<Progress> progresses = new ArrayList<>();
        for (int theNumber = 1; theNumber < 17; theNumber++) {
            for (int lieAtQuestionNumber = 0; lieAtQuestionNumber < 8; lieAtQuestionNumber++) {
                progresses.add(sevenQuestions(theNumber, lieAtQuestionNumber));
            }
        }
        //print anything without a conclusive result
        progresses.stream().filter(p -> !p.isConcludedAndOk()).forEach(Progress::printState);
    }

    private static Progress sevenQuestions(int number, int lieAt) {
        System.out.println(
                "**********************************************************************************************");
        System.out.println("Number =" + number + ", lie at question=" + lieAt);
        Question q1 = new Question(IntStream.range(1, 9).toArray());

        Question q2T = q1.followUpTrue(new Question(1, 2, 3, 4, 9, 10, 11, 12));
        Question q2F = q1.followUpFalse(new Question(5, 6, 7, 8, 13, 14, 15, 16));

        Question q3T = q2T.followUpTrue(new Question(1, 2, 5, 6, 9, 10, 13, 14));
        Question q3F = q2T.followUpFalse(new Question(3, 4, 7, 8, 11, 12, 15, 16));
        q2F.followUpTrue(q3T);
        q2F.followUpFalse(q3F);

        Question q4T = q3T.followUpTrue(new Question(1, 3, 5, 7, 9, 11, 13, 15));
        Question q4F = q3T.followUpFalse(new Question(2, 4, 6, 8, 10, 12, 14, 16));
        q3F.followUpTrue(q4T);
        q3F.followUpFalse(q4F);

        Progress progress = new Progress(lieAt, number);
        q1.ask(number, progress);
        Set<Integer> allPossible = new TreeSet<>(progress.getAllPossible());
        int maxGroupSize = (int) Math.ceil(allPossible.size() / 2.);
        Integer resultIfNoLie = progress.getIfSingleAvailable();
        allPossible.remove(resultIfNoLie);

        Question q5 = new Question(allPossible.stream().limit(maxGroupSize).toList());
        List<Integer> q5Numbers = new ArrayList<>(q5.getNumbers()); // 3, 1, 4, 7, 11
        List<Integer> q6Numbers = q5Numbers.stream().limit((int) Math.ceil(q5Numbers.size() / 2.)).toList(); // 1, 4, 7

        Question q6T = q5.followUpTrue(new Question(q6Numbers)); // 1, 4, 7
        Question q6F = q5.followUpFalse(new Question(resultIfNoLie)); // 3
        q5Numbers.removeAll(q6Numbers); // 3, 11
        q6T.followUpTrue(new Question(q6Numbers.stream().limit(1).toList())); // 1
        q6T.followUpFalse(new Question(q5Numbers)); //  3, 11
        q6F.followUpTrue(new Question(resultIfNoLie)); // 3
        q6F.followUpFalse(new Question(resultIfNoLie)); // 3

        q5.ask(number, progress);

        if (!progress.isConcluded()) {
            progress.printState();
        }

        return progress;
    }

}