package money.dto.budget;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BudgetRequest {
	private String budgetName;
	private Long categoryId;
	private Double amountLimit;
	private String period;
	private LocalDate startDate;
	private LocalDate endDate;
}
