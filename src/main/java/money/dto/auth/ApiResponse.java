package money.dto.auth;

import java.util.Map;

import lombok.Data;

@Data
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private Map<String, String> error;
}
