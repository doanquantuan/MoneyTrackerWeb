package money.dto.saving;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SavingGoalRequest {
	private String name;
	private Double targetAmount;
	private Double currentAmount;
	private LocalDate deadline;
}
