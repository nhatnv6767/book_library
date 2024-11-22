package ra.librarymanagement.model.member;

public enum MemberType {
    REGULAR("Regular"),
    VIP("VIP"),
    STUDENT("Student");
    private String displayValue;

    MemberType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}

