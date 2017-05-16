#!/bin/bash

CLUSTERING=$(find ../ -name 'Clustering.jar')

# check if javac and jar are installed
type javac >/dev/null 2>&1 || { echo >&2 "I require javac but it's not installed.  Aborting."; exit 1; }
type jar >/dev/null 2>&1 || { echo >&2 "I require jar but it's not installed.  Aborting."; exit 1; }
if [ "$CLUSTERING" == "" ] || [ ! -f "$CLUSTERING" ]; then
    echo "Clustering.jar not found, please create it first!"
    exit 1
fi
rm -rf *.jar
(
    echo "compiling..."
    cd ./.TestJarData
    rm -rf *.class
    javac -cp ".:../$CLUSTERING" *.java 1> /dev/null
    echo "packing..."
    jar cvf TestJar.jar *.class 1> /dev/null
    rm -rf *.class
    mv *.jar ../
)
echo "You know can uplaod the generated TestJar.jar file to the cluster!"
