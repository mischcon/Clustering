package clustering;

/**
 * <strong>Indicates cluster type</strong>
 * <ul>
 *     <li>
 *         Task within <b>SINGLE_INSTANCE</b> cluster type must run independently of other tasks.
 *     </li>
 *     <li>
 *         Task within <b>GROUPING</b> cluster type depends on other tasks.
 *     </li>
 * </ul>
 */
@SuppressWarnings("unused")
public enum ClusterType {
    SINGLE_INSTANCE,
    GROUPING
}
