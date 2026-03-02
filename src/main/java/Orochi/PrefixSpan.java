package Orochi;

import java.util.*;

public class PrefixSpan {
    private int minSupport;
    private List<List<Integer>> database;
    private List<SequencePattern> frequentPatterns;

    public PrefixSpan(int minSupport) {
        this.minSupport = minSupport;
        this.frequentPatterns = new ArrayList<>();
    }

    public List<SequencePattern> mine(List<List<Integer>> sequences) {
        this.database = sequences;
        List<Integer> prefix = new ArrayList<>();
        mineRecursive(prefix, database);
        return frequentPatterns;
    }

    private void mineRecursive(List<Integer> prefix, List<List<Integer>> projectedDB) {
        Map<Integer, List<List<Integer>>> itemProjections = new HashMap<>();

        for (List<Integer> sequence : projectedDB) {
            Set<Integer> counted = new HashSet<>();
            for (Integer item : sequence) {
                if (!counted.contains(item)) {
                    counted.add(item);
                    itemProjections.putIfAbsent(item, new ArrayList<>());
                }
            }
        }

        for (List<Integer> sequence : projectedDB) {
            Set<Integer> counted = new HashSet<>();
            for (int i = 0; i < sequence.size(); i++) {
                Integer item = sequence.get(i);
                if (!counted.contains(item)) {
                    counted.add(item);
                    List<Integer> suffix = sequence.subList(i + 1, sequence.size());
                    itemProjections.get(item).add(suffix);
                }
            }
        }

        for (Map.Entry<Integer, List<List<Integer>>> entry : itemProjections.entrySet()) {
            int support = entry.getValue().size();
            if (support >= minSupport) {
                List<Integer> newPrefix = new ArrayList<>(prefix);
                newPrefix.add(entry.getKey());
                frequentPatterns.add(new SequencePattern(newPrefix, support));
                mineRecursive(newPrefix, entry.getValue());
            }
        }
    }

    public static class SequencePattern {
        private List<Integer> pattern;
        private int support;

        public SequencePattern(List<Integer> pattern, int support) {
            this.pattern = new ArrayList<>(pattern);
            this.support = support;
        }

        public List<Integer> getPattern() {
            return pattern;
        }

        public int getSupport() {
            return support;
        }

        @Override
        public String toString() {
            return pattern + " : " + support;
        }
    }
}
