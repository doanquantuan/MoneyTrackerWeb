package money.dto.jar;

import lombok.Data;

@Data
public class BudgetJarTransferRequest {
	private Long fromJarId;
	private Long toJarId;
	private Double amount;
}
