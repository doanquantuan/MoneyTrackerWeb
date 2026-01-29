package money.entity;

import java.time.LocalDate;

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
import money.enums.SavingStatus;

@Entity
@Table(name = "SavingGoals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GoalID")
	private long goalId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID")
	private User user;
	
	@Column(name = "Name", columnDefinition = "NVARCHAR(255)")
	private String name;
	
	@Column(name = "TargetAmount")
	private Double targetAmount;
	
	@Column(name = "CurrentAmount")
	private Double currentAmount;
	
	@Column(name = "Deadline")
	private LocalDate deadline;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "Status", length  = 20)
	private SavingStatus status;
}
