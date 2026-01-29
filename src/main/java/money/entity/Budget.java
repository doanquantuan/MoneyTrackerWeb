package money.entity;

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
import money.enums.Period;

@Entity
@Table(name = "Budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BudgetID")
	private long budgetId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID")
	private User user;
	
	@Column(name = "BudgetName", columnDefinition = "NVARCHAR(255)")
	private String budgetName;
	
	@Column(name = "Percentage")
	private Double percentage;
	
	@Column(name = "AmountLimit")
	private Double amountLimit;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Period", length = 20)
	private Period period;
	
	@Column(name = "CurrentSpending")
	private Double currentSpending;
	
}
