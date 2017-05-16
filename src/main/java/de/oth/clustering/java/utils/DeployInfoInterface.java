package de.oth.clustering.java.utils;

/**
 * Created by mischcon on 11.05.17.
 */
public interface DeployInfoInterface {

    /**
     * Returns the version of the deployment.
     * Used to filter for suitable tasks.
     * @return
     */
    String version();
}
