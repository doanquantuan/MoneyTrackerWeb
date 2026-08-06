package money.service.impl.notif;

import money.enums.NotificationType;
import money.entity.User;

public class GenericNotificationEvent implements NotificationEvent {
	private final NotificationType type;
	private final User user;
	private final Object payload;

	public GenericNotificationEvent(NotificationType type, User user, Object payload) {
		this.type = type;
		this.user = user;
		this.payload = payload;
	}

	@Override
	public NotificationType getType() {
		return type;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Object getPayload() {
		return payload;
	}
}
