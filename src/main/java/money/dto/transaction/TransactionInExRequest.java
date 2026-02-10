package money.dto.transaction;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionInExRequest {

	private Long accountId;

	private Long categoryId;

	private String type;

	private Double amount;

	private String note;
}
