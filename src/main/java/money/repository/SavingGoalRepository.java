package money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.SavingGoal;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

	List<SavingGoal> findByUser_Email(String email);
	
}
