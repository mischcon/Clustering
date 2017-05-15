#!/bin/bash

CLUSTERING=$(find ../ -name 'Clustering.jar')

# check if javac and jar are installed
type javac >/dev/null 2>&1 || { echo >&2 "I require javac but it's not installed.  Aborting."; exit 1; }
type jar >/dev/null 2>&1 || { echo >&2 "I require jar but it's not installed.  Aborting."; exit 1; }
if [ ! -f $CLUSTERING ]; then
    echo "Clustering.jar not found, please create it first!"
    exit 1
fi

rm -rf *.jar *.class
echo "compiling..."
javac -cp ".:$CLUSTERING" *.java
echo "packing..."
jar cvf TestJar.jar *.class
rm -rf *.class

echo "You know can uplaod the generated TestJar.jar file to the cluster!"
