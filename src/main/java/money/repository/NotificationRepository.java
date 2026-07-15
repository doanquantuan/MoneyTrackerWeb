package money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
