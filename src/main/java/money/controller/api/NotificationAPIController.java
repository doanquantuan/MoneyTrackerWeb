package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import money.dto.notification.NotificationRequest;
import money.service.INotification;

@RestController
@RequestMapping("/api/notifications")
public class NotificationAPIController {

	@Autowired
	private INotification notificationService;

	@GetMapping
	public ResponseEntity<?> getNotifications() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(notificationService.getNotifList(email));
	}

	@PostMapping
	public ResponseEntity<?> createNotification(@RequestBody NotificationRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(notificationService.createNotif(email, request));
	}

	@PutMapping("/{id}/read")
	public ResponseEntity<?> markAsRead(@PathVariable Long id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		notificationService.markAsRead(id, email);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/read-all")
	public ResponseEntity<?> markAllAsRead() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		notificationService.markAllAsRead(email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		notificationService.deleteNotif(id, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> clearAllNotifications() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		notificationService.clearAllNotif(email);
		return ResponseEntity.ok().build();
	}
}
