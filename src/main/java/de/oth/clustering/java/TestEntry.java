package de.oth.clustering.java;

import clustering.Clustering;

public class TestEntry {
    String classname;
    String methodname;
    Clustering annotation;

    public TestEntry(String classname, String methodname, Clustering annotation) {
        this.classname = classname;
        this.methodname = methodname;
        this.annotation = annotation;
    }
}
