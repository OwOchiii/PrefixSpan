# PrefixSpan: Sequential Pattern Mining

A Java implementation of the PrefixSpan (Prefix-Projected Sequential Pattern Mining) algorithm with Weka integration for discovering frequent sequential patterns in sequence databases.

## What is PrefixSpan?

PrefixSpan is a pattern-growth algorithm that mines frequent sequential patterns from a sequence database. Unlike candidate generation-and-test approaches (like Apriori-based methods), PrefixSpan uses a divide-and-conquer strategy by recursively projecting sequence databases and growing sequential patterns.

## How It Works

### Core Concept

The algorithm works by:
1. Finding all frequent items (length-1 patterns)
2. For each frequent item, creating a projected database
3. Recursively mining patterns in each projected database
4. Growing patterns by appending frequent items

### Pseudocode

```
ALGORITHM PrefixSpan(prefix, projectedDB, minSupport):
    INPUT: 
        - prefix: current sequential pattern
        - projectedDB: projected sequence database
        - minSupport: minimum support threshold
    OUTPUT: 
        - Complete set of frequent sequential patterns

    1. Scan projectedDB to find all frequent items
       FOR each sequence in projectedDB:
           FOR each item in sequence:
               Count occurrence (once per sequence)
       
    2. FOR each frequent item i (support >= minSupport):
           newPattern = prefix + i
           OUTPUT newPattern with its support
           
           Create projected database for newPattern:
               FOR each sequence containing i:
                   Add suffix after first occurrence of i
           
           Recursively call PrefixSpan(newPattern, newProjectedDB, minSupport)
```

### Detailed Algorithm Steps

```
Step 1: Initialize
    - Set minimum support threshold
    - Load sequence database

Step 2: Find Length-1 Patterns
    - Scan database
    - Count frequency of each item
    - Keep items with support >= minSupport

Step 3: Build Projected Databases
    FOR each frequent item 'a':
        - Create prefix pattern <a>
        - Build projected database:
            * For each sequence containing 'a'
            * Extract suffix after 'a'
        - Store in projection map

Step 4: Recursive Mining
    FOR each projection:
        - Mine patterns in projected database
        - Grow pattern by appending frequent items
        - Create new projections
        - Repeat until no frequent items found

Step 5: Return Results
    - Return all discovered patterns with support counts
```

## Example Usage

### Basic Java Example

```java
import Orochi.PrefixSpan;
import java.util.*;

public class Example {
    public static void main(String[] args) {
        // Define sequence database
        List<List<Integer>> sequences = Arrays.asList(
            Arrays.asList(1, 2, 3, 4),  // Sequence 1
            Arrays.asList(1, 3, 4),      // Sequence 2
            Arrays.asList(1, 2, 4),      // Sequence 3
            Arrays.asList(1, 2, 3, 4),  // Sequence 4
            Arrays.asList(2, 3, 4)       // Sequence 5
        );
        
        // Set minimum support (appears in at least 2 sequences)
        int minSupport = 2;
        
        // Run PrefixSpan
        PrefixSpan prefixSpan = new PrefixSpan(minSupport);
        List<PrefixSpan.SequencePattern> patterns = prefixSpan.mine(sequences);
        
        // Display results
        for (PrefixSpan.SequencePattern pattern : patterns) {
            System.out.println(pattern);
        }
    }
}
```

### Output Explanation

```
[1] : 4          // Item 1 appears in 4 sequences
[1, 2] : 3       // Pattern 1->2 appears in 3 sequences
[1, 2, 3] : 2    // Pattern 1->2->3 appears in 2 sequences
[1, 2, 3, 4] : 2 // Pattern 1->2->3->4 appears in 2 sequences
[1, 2, 4] : 3    // Pattern 1->2->4 appears in 3 sequences
[1, 3] : 3       // Pattern 1->3 appears in 3 sequences
[1, 3, 4] : 3    // Pattern 1->3->4 appears in 3 sequences
[1, 4] : 4       // Pattern 1->4 appears in 4 sequences
[2] : 4          // Item 2 appears in 4 sequences
[2, 3] : 3       // Pattern 2->3 appears in 3 sequences
[2, 3, 4] : 3    // Pattern 2->3->4 appears in 3 sequences
[2, 4] : 4       // Pattern 2->4 appears in 4 sequences
[3] : 4          // Item 3 appears in 4 sequences
[3, 4] : 4       // Pattern 3->4 appears in 4 sequences
[4] : 5          // Item 4 appears in all 5 sequences
```

### Step-by-Step Execution

Given sequences: `<1,2,3,4>`, `<1,3,4>`, `<1,2,4>`, `<1,2,3,4>`, `<2,3,4>` with minSupport=2

**Step 1: Find frequent items**
- Item 1: support=4
- Item 2: support=4
- Item 3: support=4
- Item 4: support=5

**Step 2: Build projected database for prefix <1>**
```
Original: <1,2,3,4> -> Projection: <2,3,4>
Original: <1,3,4>   -> Projection: <3,4>
Original: <1,2,4>   -> Projection: <2,4>
Original: <1,2,3,4> -> Projection: <2,3,4>
```

**Step 3: Mine projected database for <1>**
- Frequent items in projection: 2(3), 3(3), 4(4)
- Generate patterns: <1,2>, <1,3>, <1,4>

**Step 4: Recursively project for <1,2>**
```
From <2,3,4>: <3,4>
From <2,4>:   <4>
From <2,3,4>: <3,4>
```
- Frequent items: 3(2), 4(3)
- Generate patterns: <1,2,3>, <1,2,4>

**Step 5: Continue recursion until no frequent patterns**

### Weka Integration Example

```java
import weka.core.*;
import weka.associations.PrefixSpanWeka;

public class WekaExample {
    public static void main(String[] args) throws Exception {
        // Create dataset
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("item1"));
        attributes.add(new Attribute("item2"));
        attributes.add(new Attribute("item3"));
        attributes.add(new Attribute("item4"));
        
        Instances dataset = new Instances("SequenceData", attributes, 0);
        
        // Add sequences
        double[] seq1 = {1, 2, 3, 4};
        dataset.add(new DenseInstance(1.0, seq1));
        
        // Build associations
        PrefixSpanWeka prefixSpan = new PrefixSpanWeka();
        prefixSpan.setMinSupport(2);
        prefixSpan.buildAssociations(dataset);
        
        System.out.println(prefixSpan);
    }
}
```

## Best Use Cases

### 1. Customer Purchase Behavior Analysis
**Scenario**: E-commerce platforms tracking product purchase sequences
```
Customer A: Phone -> Case -> Screen Protector
Customer B: Phone -> Charger
Customer C: Phone -> Case -> Charger
Pattern Found: Phone -> Case (frequent)
```
**Application**: Product recommendation, bundle creation, inventory planning

### 2. Web Clickstream Analysis
**Scenario**: Analyzing user navigation patterns on websites
```
User 1: Home -> Products -> Cart -> Checkout
User 2: Home -> Products -> Details -> Cart
User 3: Home -> Products -> Cart -> Checkout
Pattern: Home -> Products -> Cart (common path)
```
**Application**: UI/UX optimization, conversion funnel analysis

### 3. Medical Treatment Sequences
**Scenario**: Discovering common treatment patterns for diseases
```
Patient 1: Diagnosis -> Drug A -> Drug B -> Recovery
Patient 2: Diagnosis -> Drug A -> Recovery
Patient 3: Diagnosis -> Drug A -> Drug B -> Recovery
Pattern: Diagnosis -> Drug A (standard protocol)
```
**Application**: Clinical pathway optimization, treatment effectiveness

### 4. Bioinformatics - DNA/Protein Sequences
**Scenario**: Finding conserved motifs in biological sequences
```
Sequence 1: A-T-G-C-T-A
Sequence 2: A-T-G-C-A
Sequence 3: G-A-T-G-C-T
Pattern: A-T-G-C (conserved motif)
```
**Application**: Gene function prediction, evolutionary analysis

### 5. Network Intrusion Detection
**Scenario**: Identifying attack patterns in system logs
```
Log 1: Login -> File Access -> Data Transfer -> Logout
Log 2: Login -> File Access -> Data Transfer -> Delete
Log 3: Login -> File Access -> Data Transfer -> Logout
Pattern: Login -> File Access -> Data Transfer (suspicious)
```
**Application**: Security monitoring, anomaly detection

## Worst Use Cases (Limitations)

### 1. Very Long Sequences
**Problem**: Exponential growth of projected databases
```
Sequence length: 1000+ items
Result: Memory overflow, extremely slow processing
```
**Why**: Each projection creates new database copies, memory consumption explodes

### 2. Low Support Thresholds with Large Databases
**Problem**: Combinatorial explosion of patterns
```
Database: 10,000 sequences
MinSupport: 0.1% (10 sequences)
Result: Millions of patterns, impractical to analyze
```
**Why**: Too many patterns satisfy low threshold, loses meaningfulness

### 3. Highly Diverse Sequences (No Common Patterns)
**Problem**: No meaningful patterns discovered
```
Sequence 1: A-B-C-D
Sequence 2: E-F-G-H
Sequence 3: I-J-K-L
Result: Only length-1 patterns or nothing
```
**Why**: Algorithm designed for finding commonality, not diversity

### 4. Real-Time Streaming Data
**Problem**: Requires complete database scan
```
Requirement: Process 1000 events/second in real-time
PrefixSpan: Batch processing, needs full dataset
Result: Cannot meet real-time requirements
```
**Why**: Not designed for incremental/streaming processing

### 5. Sequences with Complex Temporal Constraints
**Problem**: Cannot handle time gaps or duration constraints
```
Requirement: Find patterns where B occurs within 1 hour after A
PrefixSpan: Only considers order, not time intervals
Result: Misses temporal relationships
```
**Why**: Algorithm only considers sequential order, not timestamps

### 6. Unordered Itemsets
**Problem**: Designed for sequences, not sets
```
Transaction: {Milk, Bread, Eggs} (order doesn't matter)
PrefixSpan: Treats as sequence, incorrect interpretation
Result: Wrong patterns
```
**Why**: Use Apriori or FP-Growth for market basket analysis instead

## Modern Applications and Problems

### 1. Social Media Behavior Analysis
**Problem**: Understanding user engagement patterns
```
User Journey: View Post -> Like -> Comment -> Share
Application: Content strategy optimization
```

### 2. IoT Sensor Event Sequences
**Problem**: Predictive maintenance in smart factories
```
Sensor Pattern: Temperature Rise -> Vibration Increase -> Failure
Application: Early warning systems
```

### 3. Financial Fraud Detection
**Problem**: Identifying fraudulent transaction sequences
```
Fraud Pattern: Small Test Transaction -> Large Purchase -> Cash Withdrawal
Application: Real-time fraud prevention
```

### 4. Educational Learning Paths
**Problem**: Discovering effective learning sequences
```
Success Pattern: Video Lecture -> Quiz -> Practice -> Advanced Topic
Application: Personalized learning recommendations
```

### 5. Supply Chain Optimization
**Problem**: Finding bottleneck patterns in logistics
```
Delay Pattern: Warehouse A -> Transit Hub B -> Delay -> Customer
Application: Route optimization, inventory placement
```

### 6. Mobile App User Flow
**Problem**: Understanding feature adoption sequences
```
Onboarding: Tutorial -> Feature A -> Feature B -> Retention
Application: Improve user onboarding, reduce churn
```

## Algorithm Complexity

- **Time Complexity**: O(n * m * 2^m) in worst case
  - n: number of sequences
  - m: average sequence length
  
- **Space Complexity**: O(n * m * k)
  - k: number of frequent patterns

- **Practical Performance**: Much better than worst case for real-world data with reasonable support thresholds

## Advantages

1. No candidate generation (unlike Apriori-based methods)
2. Efficient for long patterns
3. Divide-and-conquer approach reduces search space
4. Memory efficient compared to breadth-first approaches
5. Scalable for moderate-sized databases

## Disadvantages

1. Recursive implementation can cause stack overflow
2. Multiple database scans required
3. Performance degrades with very low support thresholds
4. Not suitable for streaming data
5. Cannot handle complex constraints (time, gaps)

## Requirements

- Java 17 or higher
- Maven 3.6+
- Weka 3.8.6 (for Weka integration)

## Building the Project

```bash
mvn clean package
```

## Running Examples

```bash
# Run standalone example
java -cp target/PrefixSpan_DataMining-1.0-SNAPSHOT.jar Orochi.Main

# Run with Weka
java -cp target/PrefixSpan_DataMining-1.0-SNAPSHOT.jar Orochi.WekaTest
```

## Project Structure

```
PrefixSpan_DataMining/
├── src/main/java/
│   ├── Orochi/
│   │   ├── PrefixSpan.java          # Core algorithm implementation
│   │   ├── Main.java                # Standalone example
│   │   └── WekaTest.java            # Weka integration test
│   └── weka/associations/
│       └── PrefixSpanWeka.java      # Weka plugin
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## References

- Pei, J., et al. (2001). "PrefixSpan: Mining Sequential Patterns Efficiently by Prefix-Projected Pattern Growth"
- Original paper: Proceedings of the 17th International Conference on Data Engineering (ICDE)

## License

This implementation is for educational and research purposes.
