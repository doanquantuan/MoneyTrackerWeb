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
import money.enums.CategoryType;

@Entity
@Table(name = "Categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CategoryID")
	private long categoryId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true) 
	@JoinColumn(name = "UserID", nullable = true)    
	private User user;
	
	@Column(name = "CategoryName", columnDefinition = "NVARCHAR(200)")
	private String categoryName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CategoryType", length = 20)
	private CategoryType categoryType;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentId") 
    @JsonIgnore 
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children;
	
	@Column(name = "Icon")
	private String icon;
	
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transactions;
	

	
}