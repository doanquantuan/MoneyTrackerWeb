package money.entity;

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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "BudgetJars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetJar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID")
	@JsonIgnore
	private User user;

	@Column(name = "name", columnDefinition = "NVARCHAR(100)")
	private String name;

	@Column(name = "jarType", length = 20)
	private String jarType; // NEC, FFA, LTSS, EDU, PLAY, GIVE

	@Column(name = "percentage")
	private Double percentage;

	@Column(name = "allocatedAmount")
	private Double allocatedAmount = 0.0;

	@Column(name = "spentAmount")
	private Double spentAmount = 0.0;

	@Column(name = "remainingAmount")
	private Double remainingAmount = 0.0;
}
