package money.service.impl.notif;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import money.enums.NotificationType;

@Component
public class NotificationStrategyFactory {

    private final Map<NotificationType, NotificationStrategy> strategies;

    public NotificationStrategyFactory(List<NotificationStrategy> list) {

        strategies = list.stream()
                .collect(Collectors.toMap(
                        NotificationStrategy::getType,
                        Function.identity()));

    }

    public NotificationStrategy getStrategy(
            NotificationType type) {

        return strategies.get(type);

    }

}