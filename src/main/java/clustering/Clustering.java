package clustering;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <strong>Main clustering annotation</strong><br>
 * <ul>
 *     <li>
 *         id - task identifier
 *     </li>
 *     <li>
 *         clusterType - SINGLE_INSTANCE or GROUPING
 *     </li>
 *     <li>
 *         <p>
 *             members - task dependency chain (single member if clusterType is SINGLE_INSTANCE)<br>
 *             array should be ordered in the way members depend on each other<br>
 *             e.g. {"files", "uploads"} - "uploads" depends on "files" so it appears after "files"<br>
 *         </p>
 *     </li>
 *     <li>
 *         expectedDuration - expected task duration
 *     </li>
 *     <li>
 *         durationUnit - unit for expectedDuration
 *     </li>
 *     <li>
 *         expectedTraffic - expected traffic load
 *     </li>
 * </ul>
 * </pre>
 */
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
