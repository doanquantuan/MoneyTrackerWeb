package money.entity;

import java.io.ObjectInputFilter.Status;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import money.enums.DebtStatus;
import money.enums.DebtType;
import money.enums.InterestRateType;
import money.enums.InterestType;


@Entity
@Table(name = "Debts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Debt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DebtID")
	private long debtId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true) 
	@JoinColumn(name = "UserID", nullable = true)    
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AccountID")
	private Account account;
	
	@Column(name = "PartnerName", columnDefinition = "NVARCHAR(250)")
	private String partnerName; 
	
	@Enumerated(EnumType.STRING)
	@Column(name = "DebtType", length = 20)
	private DebtType type; // cho vay hay đi vay
	
	@Column(name = "PrincipalAmount")
	private Double principalAmount; // tiền gốc
	
	@Column(name = "InterestRate")
	private Double interestRate; // 1%, 2%
	
	@Enumerated(EnumType.STRING)
	@Column(name = "InterestRateType", length = 20)
	private InterestRateType interestRateType; // tháng, năm

	@Enumerated(EnumType.STRING)
	@Column(name = "InterestType", length = 20)
	private InterestType interestType; // đơn, kép
	
	@Column(name = "StartDate")
	private LocalDateTime startDate;
	
	@Column(name = "DueDate")
	private LocalDateTime dueDate;
	
	@Column(name = "RepaymentPeriod")
	private Integer repaymentPeriod; // kỳ hạn
	
	@Enumerated(EnumType.STRING)
	@Column(name = "DebtStatus", length = 20)
	private DebtStatus status;
	
	@OneToMany(mappedBy = "debt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("debt")
	private List<DebtRepayment> repayments;

}
