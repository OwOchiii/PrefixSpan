package Orochi;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<List<Integer>> database = Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 3, 4),
                Arrays.asList(1, 2, 4),
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(2, 3, 4)
        );

        int minSupport = 2;
        PrefixSpan prefixSpan = new PrefixSpan(minSupport);
        List<PrefixSpan.SequencePattern> patterns = prefixSpan.mine(database);

        System.out.println("Frequent Sequential Patterns (min support = " + minSupport + "):");
        for (PrefixSpan.SequencePattern pattern : patterns) {
            System.out.println(pattern);
        }
    }
}