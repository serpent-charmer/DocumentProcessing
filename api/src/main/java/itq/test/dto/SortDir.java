package itq.test.dto;

import org.hibernate.query.SortDirection;

public enum SortDir {
    ASC, DESC;

    private SortDirection getPageSort() {
        return switch (this) {
            case ASC -> SortDirection.ASCENDING;
            case DESC -> SortDirection.DESCENDING;
        };
    }
}
