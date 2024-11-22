package ra.librarymanagement.model.BorrowRecord;

public enum BorrowStatus {
    BORROWING("Borrowing"),
    RETURNED("Returned"),
    OVERDUE("Overdue"),
    LOST("Lost");
    private String displayValue;


    BorrowStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
