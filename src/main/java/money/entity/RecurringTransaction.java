package money.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import money.enums.RecurrenceFrequency;
import money.enums.TransactionType;

@Entity
@Table(name = "RecurringTransactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RecurringID")
	private long recurringId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AccountID")
	private Account account;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CategoryID", nullable = true)
	private Category category;
	
	@Column(name = "Amount")
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TransactionType", length = 20)
	private TransactionType type;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ToAccountID", nullable = true)
	private Account toAccount;
	
	@Column(name = "Note", columnDefinition = "NVARCHAR(250)")
	private String note;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "RecurrenceFrequency", length = 20)
	private RecurrenceFrequency frequency;
	
	@Column(name = "StartDate")
	private LocalDateTime startDate;
	
	@Column(name = "NextExecutionDate")
	private LocalDateTime nextExecutionDate;
	
	@Column(name = "IsActive")
	private Boolean isActive = true;
}
