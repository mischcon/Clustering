package de.oth.clustering.java.utils;

public interface CodebaseLoaderIF {

    /**
     * Used to fill out the {@link BaseCodebaseLoader#classClusterMethods classClusterMethods} list.
     * Define your logic for filtering classes / methods from the loaded jar file here.
     */
    void fillMethodsList();
}
