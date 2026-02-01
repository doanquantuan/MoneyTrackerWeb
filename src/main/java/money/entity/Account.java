package money.entity;

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
import money.enums.AccountType;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AccountID")
	private long accountId;

	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "UserID")
	private User user;
	
	@Column(name = "AccountName", columnDefinition = "NVARCHAR(250)")
	private String accountName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "AccountType", length = 20)
	private AccountType accountType;
	
	@Column(name = "InitialBalance")
	private Double initialBalance;
	
	@Column(name = "CurrentBalance")
	private Double currentBalance;
	
	@Column(name = "Currency")
	private String currency;
	
//	@Column(name = "IsActive")
//	private Boolean isActive;
//	
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transactions;
}
