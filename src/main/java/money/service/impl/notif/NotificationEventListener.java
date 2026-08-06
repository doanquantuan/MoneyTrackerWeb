package money.service.impl.notif;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

	@Autowired
	private NotificationStrategyFactory factory;

	@EventListener
	public void handle(NotificationEvent event) {
		NotificationStrategy strategy = factory.getStrategy(event.getType());
		if (strategy != null) {
			strategy.handle(event);
		}
	}
}
