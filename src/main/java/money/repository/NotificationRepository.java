package money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import money.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	List<Notification> findByUser_Email(String email);
	
	List<Notification> findByUser_EmailOrderByCreatedAtDesc(String email);
	
	@Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user.email = :email AND n.message LIKE %:snippet%")
	boolean existsByEmailAndMessageContaining(@Param("email") String email, @Param("snippet") String snippet);
	
	
}
