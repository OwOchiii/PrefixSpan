package Orochi;

import weka.associations.PrefixSpanWeka;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaTest {
    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("test_sequences.arff");
        Instances data = source.getDataSet();
        
        PrefixSpanWeka prefixSpan = new PrefixSpanWeka();
        prefixSpan.setMinSupport(2);
        prefixSpan.buildAssociations(data);
        
        System.out.println(prefixSpan);
    }
}
