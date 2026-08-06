package money.service;

import java.util.List;

import money.dto.notification.NotificationRequest;
import money.entity.Notification;

public interface INotification {

	List<Notification> getNotifList(String email);
	
	Notification createNotif(String email, NotificationRequest request);
	
	void markAsRead(Long id, String email);
	
	void markAllAsRead(String email);
	
	void deleteNotif(Long id, String email);
	
	void clearAllNotif(String email);
}
