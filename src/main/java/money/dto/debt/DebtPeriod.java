package money.dto.debt;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DebtPeriod {
    private Integer period;
    private Double interestDue; // lãi phải trả
    private Double principalDue; // gốc phải trả
    private Double totalDue; // tổng phải trả
    private Double remainingPrincipal; // tiền gốc còn lại
    private LocalDateTime startDate;
    private LocalDateTime dueDate;

}
