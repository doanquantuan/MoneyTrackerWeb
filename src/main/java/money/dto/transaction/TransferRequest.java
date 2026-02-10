package money.dto.transaction;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransferRequest {

	private Long accountId;
	
	private Long toAccountId;

	private String type;

	private Double amount;

	private String note;
}
