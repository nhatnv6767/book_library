package ra.librarymanagement.model.member;

public enum MemberStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired");
    private String displayValue;

    MemberStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
