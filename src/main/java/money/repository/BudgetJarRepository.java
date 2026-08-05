package money.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.BudgetJar;
import money.entity.User;

@Repository
public interface BudgetJarRepository extends JpaRepository<BudgetJar, Long> {

	List<BudgetJar> findByUser(User user);

	List<BudgetJar> findByUser_Email(String email);

	Optional<BudgetJar> findByUserAndJarType(User user, String jarType);
	
	Optional<BudgetJar> findByUser_EmailAndJarType(String email, String jarType);
}
