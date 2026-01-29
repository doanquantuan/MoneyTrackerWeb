package money.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DebtRepayments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DebtRepayment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RepaymentID")
	private long repaymentId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true) 
	@JoinColumn(name = "DebtID", nullable = true) 
	private Debt debt;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true) 
	@JoinColumn(name = "TransactionID", nullable = true) 
	private Transaction transaction;
	
	@Column(name = "RepaymentDate")
	private LocalDateTime repaymentDate;
	
	@Column(name = "AmountPaid")
	private Double amountPaid;
	
	@Column(name = "PrincipalComponent")
	private Double principalComponent;
	
	@Column(name = "InterestComponent")
	private Double interestComponent;
	
	@Column(name = "RemainingBalance")
	private Double remainingBalance;
}
