package utils.db;

/**
 * <strong>Represents allowed task status</strong>
 * <ul>
 *     <li>NOT_STARTED</li>
 *     <li>RUNNING</li>
 *     <li>DONE</li>
 * </ul>
 */
public enum TaskStatus {
    NOT_STARTED("NOT_STARTED"),
    RUNNING("RUNNING"),
    DONE("DONE");

    private final String status;

    TaskStatus(final String status) {
        this.status = status;
    }

    @Override public String toString() {
        return status;
    }
}
