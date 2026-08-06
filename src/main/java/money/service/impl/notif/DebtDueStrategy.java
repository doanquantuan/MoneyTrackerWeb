package money.service.impl.notif;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import money.dto.notification.NotificationRequest;
import money.entity.Debt;
import money.entity.Notification;
import money.enums.NotificationType;
import money.repository.NotificationRepository;

@Component
public class DebtDueStrategy implements NotificationStrategy {

	@Autowired
	private NotificationRepository notificationRepo;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public NotificationType getType() {
		return NotificationType.DEBT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handle(NotificationEvent event) {
		Notification notif = new Notification();
		notif.setUser(event.getUser());
		notif.setType(NotificationType.DEBT);
		notif.setIsRead(false);
		notif.setCreatedAt(LocalDateTime.now());

		if (event.getPayload() instanceof Map) {
			Map<String, Object> data = (Map<String, Object>) event.getPayload();
			Debt debt = (Debt) data.get("debt");
			Long daysDiff = (Long) data.get("daysDiff");

			String partner = debt.getPartnerName();
			double principal = debt.getPrincipalAmount();
			String typeText = debt.getType() == money.enums.DebtType.BORROW ? "trả nợ cho" : "thu nợ từ";
			String debtSnippet = "[Mã khoản nợ: " + debt.getDebtId() + "]";
			
			String title = "";
			String message = "";
			
			if (daysDiff < 0) {
				title = "Khoản nợ quá hạn!";
				message = "Khoản nợ phải " + typeText + " " + partner + " trị giá " + principal 
						+ " VND đã quá hạn từ ngày " + debt.getDueDate().format(formatter) + ". Vui lòng tất toán sớm. " + debtSnippet;
			} else {
				title = "Khoản nợ sắp đến hạn";
				message = "Bạn có lịch cần " + typeText + " " + partner + " trị giá " + principal 
						+ " VND vào ngày " + debt.getDueDate().format(formatter) + " (còn " + daysDiff + " ngày). " + debtSnippet;
			}
			notif.setTitle(title);
			notif.setMessage(message);
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
