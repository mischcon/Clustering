package clustering;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <strong>Main clustering annotation</strong><br><br>
 * Parameter description:
 * <ul>
 *     <li>
 *         id - task identifier
 *     </li>
 *     <li>
 *         clusterType - <b>SINGLE_INSTANCE</b> or <b>GROUPING</b>
 *     </li>
 *     <li>
 *         members - task dependency tree (single member if clusterType is <b>SINGLE_INSTANCE</b>)<br>
 *         array should be ordered in the way members depend on each other<br>
 *         e.g. <i>{"files", "uploads"}</i> - <i>"uploads"</i> depends on <i>"files"</i>
 *         so it appears after <i>"files"</i><br>
 *         <pre>
 *                   ...
 *                  /
 *                 /
 *               files
 *               / \
 *              /   \
 *            print  \
 *            / \   uploads
 *           /   \     \
 *         ...   ...   ...
 *         </pre>
 *     </li>
 *     <li>
 *         expectedDuration - approx. expected task duration
 *     </li>
 *     <li>
 *         durationUnit - unit for <i>expectedDuration</i> (seconds, minutes or hours)
 *     </li>
 *     <li>
 *         expectedTraffic - expected traffic load<br>
 *         e.g. task contains a file upload of several MBs ---> traffic load = <b>MAJOR</b>
 *     </li>
 * </ul>
 * </pre>
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Clustering {
    String id();
    ClusterType clusterType() default ClusterType.GROUPING;
    String[] members() default {};
    int expectedDuration();
    DurationUnit durationUnit() default DurationUnit.SEC;
    TrafficLoad expectedTraffic() default TrafficLoad.MINOR;
}
