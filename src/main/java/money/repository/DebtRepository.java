package money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Debt;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>{

	List<Debt> findByUser_Email(String email);
	
}
