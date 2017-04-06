package utils.db;

/**
 * <strong>Represents allowed end state</strong>
 * <ul>
 *     <li>NONE - init end state</li>
 *     <li>SUCCESS</li>
 *     <li>FAILURE</li>
 *     <li>ERROR</li>
 *     <li>ABANDONED</li>
 * </ul>
 */
public enum EndState {
    NONE(null),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    ABANDONED("ABANDONED"),
    ERROR("ERROR");

    private final String result;

    EndState(final String result) {
        this.result = result;
    }

    @Override public String toString() {
        return result;
    }
}
