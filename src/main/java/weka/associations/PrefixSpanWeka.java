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

        int maxNumeric = 0;
        for (int i = 0; i < data.numInstances(); i++) {
            Instance inst = data.instance(i);
            for (int j = 0; j < inst.numAttributes(); j++) {
                if (!inst.isMissing(j) && !inst.attribute(j).isNominal()) {
                    maxNumeric = Math.max(maxNumeric, (int) inst.value(j));
                }
            }
        }

        Map<String, Integer> nominalMap = new HashMap<>();
        int nextNominalId = maxNumeric + 1;

        for (int i = 0; i < data.numInstances(); i++) {
            Instance inst = data.instance(i);
            List<Integer> sequence = new ArrayList<>();
            for (int j = 0; j < inst.numAttributes(); j++) {
                if (j == data.classIndex()) continue;
                if (inst.isMissing(j)) {
                    sequence.add(-1); // placeholder: "unknown step"
                } else if (inst.attribute(j).isNominal()) {
                    String val = inst.stringValue(j);
                    if (!nominalMap.containsKey(val)) {
                        nominalMap.put(val, nextNominalId++);
                    }
                    sequence.add(nominalMap.get(val));
                } else {
                    sequence.add((int) inst.value(j));
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
