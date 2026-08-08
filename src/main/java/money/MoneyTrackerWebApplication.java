package money;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyTrackerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyTrackerWebApplication.class, args);
	}

}
