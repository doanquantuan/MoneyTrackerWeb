package money.dto.transaction;


import lombok.Data;

@Data
public class TransactionInExRequest {

	private Long accountId;

	private Long categoryId;

	private Long budgetJarId;

	private Boolean autoAllocateToJars;

	private String type;

	private Double amount;

	private String note;
}
