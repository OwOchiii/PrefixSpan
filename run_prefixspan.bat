@echo off
REM Update WEKA_HOME to your Weka installation directory
set WEKA_HOME=D:\Weka-3-8-6

java -cp "target\PrefixSpan_DataMining-1.0-SNAPSHOT.jar;%WEKA_HOME%\weka.jar" weka.associations.PrefixSpanWeka -t test_sequences.arff -S 2
pause
