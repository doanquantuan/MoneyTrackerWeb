package money.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

	List<Category> findByUser_Email(String email);
	
	Optional<Category> findById(long id);
	
	
}
