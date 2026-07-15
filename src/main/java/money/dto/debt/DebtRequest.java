package money.dto.debt;

import java.io.ObjectInputFilter.Status;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DebtRequest {

	private Long accountId;
	private String partnerName;	
	private String type;
	private Double principalAmount;
	private Double interestRate;
	private String interestType;
	private LocalDateTime startDate;
	private LocalDateTime dueDate;
	
}
