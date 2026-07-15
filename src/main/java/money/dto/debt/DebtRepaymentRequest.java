package money.dto.debt;

import lombok.Data;

@Data
public class DebtRepaymentRequest {
    private Double amountPaid;
    private Double principalComponent;
    private Double interestComponent;
    private Long accountId;
    private String note;
}
