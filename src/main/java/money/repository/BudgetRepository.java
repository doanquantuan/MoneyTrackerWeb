package money.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import money.entity.Budget;
import money.entity.Category;
import money.entity.User;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

	List<Budget> findByUser_Email(String email);
	
	@Query("SELECT b FROM Budget b WHERE b.user = :user " +
	       "AND b.startDate <= :date AND b.endDate >= :date " +
	       "AND (b.category IS NULL OR b.category = :category)")
	List<Budget> findActiveBudgets(
		@Param("user") User user,
		@Param("category") Category category,
		@Param("date") LocalDate date
	);
}
