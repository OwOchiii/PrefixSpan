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

### Real-World Example: Morning Routine Analysis

Let's analyze 5 people's morning routines to find common patterns:

**Database (Encoded):**
```
Person 1: Wake(1) -> Shower(2) -> Breakfast(3) -> Coffee(4) -> Commute(5)
Person 2: Wake(1) -> Breakfast(3) -> Coffee(4) -> Commute(5)
Person 3: Wake(1) -> Shower(2) -> Coffee(4) -> Commute(5)
Person 4: Wake(1) -> Shower(2) -> Breakfast(3) -> Coffee(4) -> Commute(5)
Person 5: Wake(1) -> Coffee(4) -> Commute(5)
```

**Execution with minSupport=3:**

**Step 1: Scan database for frequent items**
```
Item counts:
- Wake(1): 5 sequences ✓ (≥3)
- Shower(2): 3 sequences ✓ (≥3)
- Breakfast(3): 3 sequences ✓ (≥3)
- Coffee(4): 5 sequences ✓ (≥3)
- Commute(5): 5 sequences ✓ (≥3)

All items are frequent!
```

**Step 2: Build projected database for Wake(1)**
```
Person 1: <1,2,3,4,5> → Projection: <2,3,4,5>
Person 2: <1,3,4,5>   → Projection: <3,4,5>
Person 3: <1,2,4,5>   → Projection: <2,4,5>
Person 4: <1,2,3,4,5> → Projection: <2,3,4,5>
Person 5: <1,4,5>     → Projection: <4,5>

Projected DB for <1>: {<2,3,4,5>, <3,4,5>, <2,4,5>, <2,3,4,5>, <4,5>}
```

**Step 3: Find frequent items in <1>'s projection**
```
Counting in projected DB:
- Shower(2): 3 sequences ✓
- Breakfast(3): 3 sequences ✓
- Coffee(4): 5 sequences ✓
- Commute(5): 5 sequences ✓

Generate patterns:
- <1,2> support=3 (Wake → Shower)
- <1,3> support=3 (Wake → Breakfast)
- <1,4> support=5 (Wake → Coffee)
- <1,5> support=5 (Wake → Commute)
```

**Step 4: Build projected database for <1,4> (Wake → Coffee)**
```
From <2,3,4,5>: after 4 → <5>
From <3,4,5>:   after 4 → <5>
From <2,4,5>:   after 4 → <5>
From <2,3,4,5>: after 4 → <5>
From <4,5>:     after 4 → <5>

Projected DB for <1,4>: {<5>, <5>, <5>, <5>, <5>}
```

**Step 5: Find frequent items in <1,4>'s projection**
```
- Commute(5): 5 sequences ✓

Generate pattern:
- <1,4,5> support=5 (Wake → Coffee → Commute)
```

**Step 6: Build projected database for <1,4,5>**
```
All projections are empty (no items after Commute)
Recursion stops.
```

**Step 7: Continue with other branches...**

**Final Patterns Found (minSupport=3):**
```
[1] : 5          Wake (everyone wakes up)
[1,2] : 3        Wake → Shower
[1,2,4] : 3      Wake → Shower → Coffee
[1,2,4,5] : 3    Wake → Shower → Coffee → Commute
[1,2,5] : 3      Wake → Shower → Commute
[1,3] : 3        Wake → Breakfast
[1,3,4] : 3      Wake → Breakfast → Coffee
[1,3,4,5] : 3    Wake → Breakfast → Coffee → Commute
[1,3,5] : 3      Wake → Breakfast → Commute
[1,4] : 5        Wake → Coffee (most common!)
[1,4,5] : 5      Wake → Coffee → Commute (universal pattern)
[1,5] : 5        Wake → Commute
[2] : 3          Shower
[2,4] : 3        Shower → Coffee
[2,4,5] : 3      Shower → Coffee → Commute
[2,5] : 3        Shower → Commute
[3] : 3          Breakfast
[3,4] : 3        Breakfast → Coffee
[3,4,5] : 3      Breakfast → Coffee → Commute
[3,5] : 3        Breakfast → Commute
[4] : 5          Coffee (everyone drinks coffee!)
[4,5] : 5        Coffee → Commute
[5] : 5          Commute
```

**Insights:**
- 100% of people: Wake → Coffee → Commute
- 60% of people: Include shower in routine
- 60% of people: Eat breakfast
- Coffee is ALWAYS followed by commute
- Most skip either shower OR breakfast, rarely both

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

### 1. No Candidate Generation
**Why it matters**: Traditional Apriori-based methods generate and test millions of candidate patterns
```
Apriori approach:
- Generate all possible 2-item combinations: C(n,2)
- Test each against database
- Generate 3-item combinations from frequent 2-items
- Repeat...

PrefixSpan approach:
- Only grow patterns that are actually frequent
- No wasted computation on non-existent patterns
```
**Benefit**: 10-100x faster on large datasets

### 2. Efficient for Long Patterns
**Why it matters**: Can find patterns of length 10+ without exponential slowdown
```
Example: Finding "Wake → Shower → Dress → Breakfast → Coffee → Commute"
- Apriori: Must generate/test all 6-item subsets first
- PrefixSpan: Grows pattern step-by-step only if frequent
```
**Benefit**: Discovers deep patterns that Apriori would timeout on

### 3. Divide-and-Conquer Strategy
**Why it matters**: Breaks large problem into smaller independent subproblems
```
Original DB: 10,000 sequences
After projecting on "Wake":
- Projection 1: 8,000 sequences (people who wake up)
- Work only on this smaller subset
- Further projections get even smaller
```
**Benefit**: Reduces search space exponentially, enables parallelization

### 4. Memory Efficient (Compared to Breadth-First)
**Why it matters**: Depth-first recursion uses less memory than storing all patterns at once
```
Breadth-first (FP-Growth style):
- Store all 1-item patterns: 1000 patterns
- Store all 2-item patterns: 10,000 patterns
- Store all 3-item patterns: 50,000 patterns
- Total: 61,000 patterns in memory

PrefixSpan (depth-first):
- Store current path: max 10 items
- Store current projection: subset of DB
- Output patterns immediately
```
**Benefit**: Can handle larger databases without running out of RAM

### 5. Scalable for Moderate Databases
**Why it matters**: Works well on real-world datasets (1K-100K sequences)
```
Performance on 50,000 customer purchase sequences:
- Apriori: 45 minutes
- PrefixSpan: 3 minutes
```
**Benefit**: Practical for business analytics and production systems

## Disadvantages

### 1. Recursive Implementation Can Cause Stack Overflow
**Why it's a problem**: Deep recursion on very long patterns
```
Scenario: Pattern of length 50
- Each level adds stack frame
- Java default stack: ~1MB
- Deep recursion: Stack overflow error
```
**Impact**: Cannot handle extremely long sequential patterns
**Workaround**: Increase stack size with -Xss flag or use iterative version

### 2. Multiple Database Scans Required
**Why it's a problem**: Each projection requires scanning its database
```
For pattern <1,2,3>:
- Scan 1: Find frequent items (full DB)
- Scan 2: Build projection for <1>
- Scan 3: Build projection for <1,2>
- Scan 4: Build projection for <1,2,3>
```
**Impact**: Slow on disk-based databases, I/O bottleneck
**Workaround**: Keep data in memory, use SSD storage

### 3. Performance Degrades with Very Low Support
**Why it's a problem**: Too many patterns to explore
```
Database: 10,000 sequences
minSupport=0.01% (1 sequence):
- Nearly every subsequence is "frequent"
- Millions of patterns generated
- Takes hours to complete
```
**Impact**: Impractical for rare pattern mining
**Workaround**: Use sampling or constraint-based mining

### 4. Not Suitable for Streaming Data
**Why it's a problem**: Requires complete dataset upfront
```
Streaming scenario:
- New sequences arrive continuously
- PrefixSpan needs to restart from scratch
- Cannot incrementally update patterns
```
**Impact**: Cannot use for real-time analytics
**Workaround**: Use sliding window or incremental algorithms

### 5. Cannot Handle Complex Constraints
**Why it's a problem**: Only considers order, not time or gaps
```
Real requirement: "Find patterns where B occurs within 1 hour after A"
PrefixSpan limitation:
- Only knows: A comes before B
- Doesn't know: How long between A and B
- Cannot enforce: Time constraints
```
**Impact**: Limited for temporal pattern mining
**Workaround**: Pre-process data or use specialized algorithms (cSPADE, SPADE)

### 6. Projection Overhead
**Why it's a problem**: Creating projected databases takes time and memory
```
For each frequent item:
- Copy relevant sequences
- Extract suffixes
- Store in new structure
- Repeat recursively

Memory usage spikes during projection creation
```
**Impact**: Memory pressure on large databases
**Workaround**: Use pseudo-projection (pointers instead of copies)

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
