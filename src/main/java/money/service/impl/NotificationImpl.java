package money.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.notification.NotificationRequest;
import money.entity.Debt;
import money.entity.Notification;
import money.entity.User;
import money.enums.DebtStatus;
import money.enums.NotificationType;
import money.service.impl.notif.GenericNotificationEvent;
import money.repository.DebtRepository;
import money.repository.NotificationRepository;
import money.repository.UserRepository;
import money.service.INotification;

@Service
@Transactional
public class NotificationImpl implements INotification {
	
	@Autowired
	private NotificationRepository notificationRepo;
	
	@Autowired
	private UserRepository userRepo; 

	@Autowired
	private DebtRepository debtRepo;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public List<Notification> getNotifList(String email) {
		// Tự động kiểm tra các khoản nợ và phát sự kiện thông báo nếu có khoản nợ đến
		// hạn
		checkAndCreateDebtNotifications(email);

		return notificationRepo.findByUser_EmailOrderByCreatedAtDesc(email);
	}

	@Override
	public Notification createNotif(String email, NotificationRequest request) {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User không tồn tại"));
		
		NotificationType notifType;
		try {
			notifType = NotificationType.valueOf(request.getType().toUpperCase());
		} catch (Exception e) {
			notifType = NotificationType.BUDGET;
		}
		
		// Phát sự kiện thông báo qua Observer
		GenericNotificationEvent event = new GenericNotificationEvent(notifType, user, request);
		eventPublisher.publishEvent(event);

		// Trả về thông báo mới nhất của user
		List<Notification> list = notificationRepo.findByUser_EmailOrderByCreatedAtDesc(email);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public void markAsRead(Long id, String email) {
		Notification notif = notificationRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));
		if (!notif.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền sửa thông báo này");
		}
		notif.setIsRead(true);
		notificationRepo.save(notif);
	}

	@Override
	public void markAllAsRead(String email) {
		List<Notification> notifs = notificationRepo.findByUser_Email(email);
		for (Notification notif : notifs) {
			if (!notif.getIsRead()) {
				notif.setIsRead(true);
				notificationRepo.save(notif);
			}
		}
	}

	@Override
	public void deleteNotif(Long id, String email) {
		Notification notif = notificationRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));
		if (!notif.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền xóa thông báo này");
		}
		notificationRepo.delete(notif);
	}

	@Override
	public void clearAllNotif(String email) {
		List<Notification> notifs = notificationRepo.findByUser_Email(email);
		notificationRepo.deleteAll(notifs);
	}

	private void checkAndCreateDebtNotifications(String email) {
		try {
			User user = userRepo.findByEmail(email).orElse(null);
			if (user == null) return;

			List<Debt> debts = debtRepo.findByUser_Email(email);

			for (Debt debt : debts) {
				if (debt.getStatus() == DebtStatus.ACTIVE || debt.getStatus() == DebtStatus.OVERDUE) {
					if (debt.getDueDate() != null) {
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime dueDate = debt.getDueDate();
						long daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate());
						
						String debtSnippet = "[Mã khoản nợ: " + debt.getDebtId() + "]";
						
						// Kiểm tra xem đã có thông báo nào chứa mã khoản nợ này chưa
						boolean alreadyNotified = notificationRepo.existsByEmailAndMessageContaining(email, debtSnippet);
						
						if (!alreadyNotified && (daysDiff <= 3)) {
							Map<String, Object> payload = new HashMap<>();
							payload.put("debt", debt);
							payload.put("daysDiff", daysDiff);

							// Phát sự kiện thông báo nợ đến hạn (loại DEBT)
							eventPublisher.publishEvent(new GenericNotificationEvent(NotificationType.DEBT, user, payload));
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Lỗi khi tự động kiểm tra thông báo khoản nợ: " + e.getMessage());
		}
	}
}

