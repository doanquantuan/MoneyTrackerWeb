package money.dto.transaction;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionRequest {

	private Long accountId;

	private Long categoryId;

	private String type;

	private Double amount;

	private LocalDateTime transactionDate;

	private String note;

}
