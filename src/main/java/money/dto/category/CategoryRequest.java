package money.dto.category;

import lombok.Data;

@Data
public class CategoryRequest {

	private String categoryName;
	
	private String categoryType;
	
	private Long parentId;

	private String icon;
}
