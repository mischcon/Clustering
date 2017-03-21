import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <pre>
 * Main clustering annotation.
 *
 * id               - task identifier
 * clusterType      - SINGLE_INSTANCE or GROUPING
 * members          - task dependency chain (single member if clusterType is SINGLE_INSTANCE)
 *                    array should be ordered in the way members depend on each other
 *                    e.g. {"files", "uploads"} - "uploads" depends on "files" so it appears after "files"
 * expectedDuration - expected task duration
 * durationUnit     - unit for expectedDuration
 * expectedTraffic  - expected traffic load
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
