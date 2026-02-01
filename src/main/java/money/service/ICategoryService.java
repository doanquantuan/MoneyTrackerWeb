package money.service;

import java.util.List;

import money.dto.category.CategoryRequest;
import money.entity.Category;

public interface ICategoryService {

	List<Category> getListCategory(String email);
	
	Category addCategory(String email, CategoryRequest request);
}
