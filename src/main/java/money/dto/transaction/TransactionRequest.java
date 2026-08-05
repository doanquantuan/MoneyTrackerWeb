package money.dto.transaction;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionRequest {

	private Long accountId;
	
	private Long toAccountId;

	private Long categoryId;

	private Long budgetJarId;

	private Boolean autoAllocateToJars;

	private String type;

	private Double amount;

	private String note;

}
