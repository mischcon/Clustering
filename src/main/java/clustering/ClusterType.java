package clustering;

/**
 *
 * <strong>Indicates cluster type</strong>
 * <ul>
 *     <li>
 *         Task within SINGLE_INSTANCE cluster type should run independently of other tasks.
 *     </li>
 *     <li>
 *         Task within GROUPING cluster type depends on other tasks.
 *     </li>
 * </ul>
 */
public enum ClusterType {
    SINGLE_INSTANCE,
    GROUPING
}
