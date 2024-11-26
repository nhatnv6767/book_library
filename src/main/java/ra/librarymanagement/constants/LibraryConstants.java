package ra.librarymanagement.constants;

import java.math.BigDecimal;

public class LibraryConstants {
    public static final int DEFAULT_BORROW_DAYS = 14;
    public static final int MAX_EXTENSIONS = 2;
    public static final BigDecimal DAILY_FINE = new BigDecimal("1000");
    public static final BigDecimal LOST_BOOK_FINE = new BigDecimal("100000");

    // Thêm constants cho member types
    public static final int REGULAR_MEMBER_MAX_BOOKS = 3;
    public static final int VIP_MEMBER_MAX_BOOKS = 5;
    public static final int STUDENT_MEMBER_MAX_BOOKS = 2;

    // Member code constants
    public static final String MEMBER_CODE_PREFIX = "MEM";
    public static final int MEMBER_CODE_LENGTH = 10;
    public static final int MAX_SEQUENCE_NUMBER = 9999999;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
}
