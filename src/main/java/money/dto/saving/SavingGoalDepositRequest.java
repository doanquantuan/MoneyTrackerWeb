package money.dto.saving;

import lombok.Data;

@Data
public class SavingGoalDepositRequest {
	private Double amount;
	private Long accountId;
	private String note;
}
