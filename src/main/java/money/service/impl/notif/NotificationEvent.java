package money.service.impl.notif;

import money.enums.NotificationType;
import money.entity.User;

public interface NotificationEvent {
	NotificationType getType();
	User getUser();
	Object getPayload();
}
