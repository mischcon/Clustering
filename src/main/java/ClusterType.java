/**
 * <pre>
 * Indicates cluster type.
 *
 * Task within SINGLE_INSTANCE cluster type should run independently of other tasks.
 * Task within GROUPING cluster type depends on other tasks.
 * </pre>
 */
public enum ClusterType {
    SINGLE_INSTANCE,
    GROUPING
}
