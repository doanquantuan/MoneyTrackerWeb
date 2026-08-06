package money.service.impl.notif;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import money.dto.notification.NotificationRequest;
import money.entity.BudgetJar;
import money.entity.Notification;
import money.enums.NotificationType;
import money.repository.NotificationRepository;

@Component
public class BudgetWarningStrategy implements NotificationStrategy {

	@Autowired
	private NotificationRepository notificationRepo;

	@Override
	public NotificationType getType() {
		return NotificationType.BUDGET;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handle(NotificationEvent event) {
		Notification notif = new Notification();
		notif.setUser(event.getUser());
		notif.setType(NotificationType.BUDGET);
		notif.setIsRead(false);
		notif.setCreatedAt(LocalDateTime.now());

		if (event.getPayload() instanceof Map) {
			Map<String, Object> data = (Map<String, Object>) event.getPayload();
			BudgetJar jar = (BudgetJar) data.get("jar");
			Double newSpent = (Double) data.get("newSpent");
			notif.setTitle("Cảnh báo vượt hạn mức hũ");
			notif.setMessage("Hũ ngân sách '" + jar.getName() 
					+ "' có hạn mức phân bổ là " + jar.getAllocatedAmount() + " VND, hiện tại bạn đã chi tiêu vượt quá giới hạn với tổng cộng: " 
					+ newSpent + " VND!");
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
