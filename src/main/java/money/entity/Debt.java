package money.entity;

import java.io.ObjectInputFilter.Status;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import money.enums.DebtType;
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
	
	@Column(name = "PartnerName", columnDefinition = "NVARCHAR(250)")
	private String partnerName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "DebtType", length = 20)
	private DebtType type;
	
	@Column(name = "PrincipalAmount")
	private Double principalAmount;
	
	@Column(name = "InterestRate")
	private Double interestRate;

	@Enumerated(EnumType.STRING)
	@Column(name = "InterestType", length = 20)
	private InterestType interestType;
	
	@Column(name = "StartDate")
	private LocalDateTime startDate;
	
	@Column(name = "DueDate")
	private LocalDateTime dueDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", length = 20)
	private Status status;
	
	@OneToMany(mappedBy = "debt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<DebtRepayment> repayments;
}
