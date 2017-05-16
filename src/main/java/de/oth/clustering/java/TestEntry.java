package de.oth.clustering.java;

import de.oth.clustering.java.clustering.Clustering;

/**
 * Output of {@link TestingCodebaseLoader#getClassClusterMethods()} - represents one test entry.
 * Contains the classname + methodname of the entry (used to easily identify the test) and
 * its {@link de.oth.clustering.java.clustering.Clustering} annotation (needed for the dependency tree creation)
 */
public class TestEntry extends Entry{
    public String classname;
    public String methodname;
    public Clustering annotation;

    public TestEntry(String classname, String methodname, Clustering annotation) {
        this.classname = classname;
        this.methodname = methodname;
        this.annotation = annotation;
    }
}
