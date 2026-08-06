package money.service.impl.notif;

import money.enums.NotificationType;

public interface NotificationStrategy {
	NotificationType getType();
	void handle(NotificationEvent event);
}
