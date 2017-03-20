/**
 * Created by mischcon on 3/20/17.
 */
public @interface Clustering {

    public enum ClusterType { GROUPING, SINGLE_INSTANCE }

    String Path();
    ClusterType ClusterType();
    int ExpectedDuration();
    int ExpectedTraffic();

}
