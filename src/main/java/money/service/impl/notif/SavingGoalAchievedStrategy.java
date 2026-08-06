package money.service.impl.notif;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import money.dto.notification.NotificationRequest;
import money.entity.Notification;
import money.entity.SavingGoal;
import money.enums.NotificationType;
import money.repository.NotificationRepository;

@Component
public class SavingGoalAchievedStrategy implements NotificationStrategy {

	@Autowired
	private NotificationRepository notificationRepo;

	@Override
	public NotificationType getType() {
		return NotificationType.SAVING_GOAL;
	}

	@Override
	public void handle(NotificationEvent event) {
		Notification notif = new Notification();
		notif.setUser(event.getUser());
		notif.setType(NotificationType.SAVING_GOAL);
		notif.setIsRead(false);
		notif.setCreatedAt(LocalDateTime.now());

		if (event.getPayload() instanceof SavingGoal) {
			SavingGoal goal = (SavingGoal) event.getPayload();
			notif.setTitle("Đạt mục tiêu tiết kiệm!");
			notif.setMessage("Chúc mừng! Bạn đã hoàn thành mục tiêu tiết kiệm '" + goal.getName() 
					+ "' với tổng số tiền tích lũy: " + goal.getCurrentAmount() + " / " + goal.getTargetAmount() + " VND!");
		} else if (event.getPayload() instanceof NotificationRequest) {
			NotificationRequest req = (NotificationRequest) event.getPayload();
			notif.setTitle(req.getTitle());
			notif.setMessage(req.getMessage());
		} else {
			return;
		}

		notificationRepo.save(notif);
	}
}
