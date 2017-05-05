package de.oth.clustering.java;

public interface CodebaseLoaderIF {

    /**
     * Used to fill out the {@link de.oth.clustering.java.BaseCodebaseLoader#classClusterMethods classClusterMethods} list.
     * Define your logic for filtering classes / methods from the loaded jar file here.
     */
    void fillMethodsList();
}
