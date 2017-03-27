/**
 * <strong>Test application for @Clustering</strong><br>
 * will be removed later
 */
public class AnnotationTest {

    /**
     * @Clustering for methods
     */

    @Clustering(
            id="get_nodes",
            clusterType=ClusterType.GROUPING,
            members={"nodes"},
            expectedDuration=3,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    public static void testGetNodes() { System.out.println("> testGetNodes"); }

    @Clustering(
            id="create_file",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files"},
            expectedDuration=5,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    public static void testCreateFile() { System.out.println("> testCreateFile"); }

    @Clustering(
            id="file_upload",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files", "uploads"},
            expectedDuration=10,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MAJOR)
    public static void testUploadFile() { System.out.println("> testUploadFile"); }

    @Clustering(
            id="get_config",
            clusterType=ClusterType.SINGLE_INSTANCE,
            members={"config"},
            expectedDuration=1,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR)
    public static void testGetConfig() { System.out.println("> testGetConfig"); }


    /**
     * @Clustering for classes
     */
    @Clustering(
            id="groups",
            clusterType=ClusterType.GROUPING,
            members={"groups"},
            expectedDuration=5,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR
    )
    public static class TestGroups {
        static void setup() { System.out.println("> setup"); }
        static void teardown() { System.out.println("> teardown"); }
        public static void testGetGroup() { System.out.println("> testGetGroup"); }
        public static void testCreateGroup() { System.out.println("> testCreateGroup"); }
        public static void testDeleteGroup() { System.out.println("> testDeleteGroup"); }
    }

    public static void main(String[] args) {
        testGetNodes();
        testCreateFile();
        testUploadFile();
        testGetConfig();
        TestGroups.setup();
        TestGroups.testGetGroup();
        TestGroups.teardown();
        TestGroups.setup();
        TestGroups.testCreateGroup();
        TestGroups.teardown();
        TestGroups.setup();
        TestGroups.testDeleteGroup();
        TestGroups.teardown();
    }
}
