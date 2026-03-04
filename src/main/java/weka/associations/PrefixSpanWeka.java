package weka.associations;

import Orochi.PrefixSpan;
import weka.core.*;
import java.util.*;
import Orochi.PrefixSpan.SequencePattern;

public class PrefixSpanWeka extends AbstractAssociator implements OptionHandler {
    private int minSupport = 2;
    private List<SequencePattern> patterns;


    @Override
    public void buildAssociations(Instances data) throws Exception {
        List<List<Integer>> sequences = new ArrayList<>();

        // Build a global mapping: (attributeIndex, nominalIndex) -> unique global integer
        // This prevents "value 0 of attribute 1" from colliding with "value 0 of attribute 2"
        Map<String, Integer> globalItemMap = new HashMap<>();
        int nextId = 1; // start at 1, keep 0 reserved or just avoid confusion

        for (int i = 0; i < data.numInstances(); i++) {
            Instance inst = data.instance(i);
            List<Integer> sequence = new ArrayList<>();
            for (int j = 0; j < inst.numAttributes(); j++) {
                if (!inst.isMissing(j)) {
                    String key;
                    if (inst.attribute(j).isNominal()) {
                        // Use the actual string value as part of the key
                        key = j + "_" + inst.stringValue(j);
                    } else {
                        key = j + "_" + (int) inst.value(j);
                    }
                    if (!globalItemMap.containsKey(key)) {
                        globalItemMap.put(key, nextId++);
                    }
                    sequence.add(globalItemMap.get(key));
                }
            }
            if (!sequence.isEmpty()) {
                sequences.add(sequence);
            }
        }

        PrefixSpan prefixSpan = new PrefixSpan(minSupport);
        patterns = prefixSpan.mine(sequences);
    }

    @Override
    public String toString() {
        if (patterns == null) return "No patterns found.";

        StringBuilder sb = new StringBuilder();
        sb.append("PrefixSpan Frequent Sequential Patterns\n");
        sb.append("Minimum Support: ").append(minSupport).append("\n");
        sb.append("Number of patterns: ").append(patterns.size()).append("\n\n");

        for (SequencePattern pattern : patterns) {
            sb.append(pattern.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String[] getOptions() {
        Vector<String> options = new Vector<>();
        options.add("-S");
        options.add(String.valueOf(minSupport));
        return options.toArray(new String[0]);
    }

    @Override
    public void setOptions(String[] options) throws Exception {
        String supportString = Utils.getOption('S', options);
        if (supportString.length() != 0) {
            minSupport = Integer.parseInt(supportString);
        }
    }

    @Override
    public Enumeration<Option> listOptions() {
        Vector<Option> options = new Vector<>();
        options.addElement(new Option("\tMinimum support (default: 2)", "S", 1, "-S <num>"));
        return options.elements();
    }

    public void setMinSupport(int support) {
        this.minSupport = support;
    }

    public int getMinSupport() {
        return minSupport;
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.MISSING_VALUES);
        result.enable(Capabilities.Capability.NO_CLASS);

        return result;
    }

    public static void main(String[] args) {
        runAssociator(new PrefixSpanWeka(), args);
    }
}
