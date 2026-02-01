package money.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import money.dto.category.CategoryRequest;
import money.entity.Account;
import money.entity.Category;
import money.entity.User;
import money.enums.AccountType;
import money.enums.CategoryType;
import money.repository.CategoryRepository;
import money.repository.UserRepository;
import money.service.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService{
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CategoryRepository cateRepo;

	@Override
	public List<Category> getListCategory(String email) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
		List<Category> categories = cateRepo.findByUser_Email(email);
		return categories;
	}
	
	@Override
	public Category addCategory(String email, CategoryRequest request) {
		User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tìm thấy"));
		
		Category cate = new Category();
		cate.setUser(user);
		cate.setCategoryName(request.getCategoryName());
		cate.setCategoryType(CategoryType.valueOf(request.getCategoryType().toUpperCase()));
		cate.setIcon(request.getIcon());
		if (request.getParentId() != null) {
	        Category parent = cateRepo.findById(request.getParentId())
	                .orElseThrow(() -> new RuntimeException("Parent category not found"));
	        
	        if (parent.getUser() != null && parent.getUser().getUserId() != user.getUserId()) {
	            throw new RuntimeException("You do not have permission to use this parent category");
	        }
	        cate.setParent(parent);
	    } else {
	        cate.setParent(null);
	    }


	    return cateRepo.save(cate);
	}

}
