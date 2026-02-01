package money.dto.category;

import lombok.Data;
import money.enums.CategoryType;

@Data
public class CategoryRequest {

	private String categoryName;
	
	private String categoryType;
	
	private Long parentId;

	private String icon;
}
