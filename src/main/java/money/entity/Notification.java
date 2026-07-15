package money.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import money.enums.NotificationType;

@Entity
@Table(name = "Notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NotificationID")
	private long notificationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserID")
	private User user;
	
	@Column(name = "Title", columnDefinition = "NVARCHAR(255)")
	private String title;
	
	@Column(name = "Message", columnDefinition = "NVARCHAR(1000)")
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "NotificationType", length = 20)
	private NotificationType type;
	
	@Column(name = "IsRead")
	private Boolean isRead = false;
	
	@Column(name = "CreatedAt")
	private LocalDateTime createdAt = LocalDateTime.now();
}
