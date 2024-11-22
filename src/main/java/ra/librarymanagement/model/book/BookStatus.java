package ra.librarymanagement.model.book;

public enum BookStatus {
    AVAILABLE("Available"),
    OUT_OF_STOCK("Out of stock"),
    DISCONTINUED("Disconnected");

    private String displayValue;

    BookStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
