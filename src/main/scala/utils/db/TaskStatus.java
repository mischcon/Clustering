package utils.db;

public enum TaskStatus {
    NOT_STARTED("NOT_STARTED"),
    RUNNING("RUNNING"),
    DONE("DONE");

    private final String status;

    TaskStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
