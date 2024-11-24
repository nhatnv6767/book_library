package ra.librarymanagement.model.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alert {
    private String type; // OVERDUE or DUE_SOON
    private String message;
    private LocalDateTime dueDate;
}
