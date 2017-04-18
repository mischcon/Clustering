package de.oth.clustering.java;

import clustering.Clustering;

public class TestEntry {
    public String classname;
    public String methodname;
    public Clustering annotation;

    public TestEntry(String classname, String methodname, Clustering annotation) {
        this.classname = classname;
        this.methodname = methodname;
        this.annotation = annotation;
    }
}
