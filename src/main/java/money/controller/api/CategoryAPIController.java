package money.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import money.dto.category.CategoryRequest;
import money.entity.Account;
import money.entity.Category;
import money.service.ICategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryAPIController {
	
	@Autowired
	private ICategoryService categoryService;
	
	private String getCurrentUserEmail() {
	        return SecurityContextHolder.getContext().getAuthentication().getName();
	    }
	 
	@GetMapping
	public ResponseEntity<?> getCategories(){
		String email = getCurrentUserEmail();
        return ResponseEntity.ok(categoryService.getListCategory(email));
	}

	@PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest request) {
		try {
			String email = getCurrentUserEmail();
	
		    Category cate = categoryService.addCategory(email, request);
		    return ResponseEntity.ok(cate);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
   
    }
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
		try {
			String email = getCurrentUserEmail();
	
		    Category cate = categoryService.editCategory(email, id, request);
		    return ResponseEntity.ok(cate);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }
//	
//	@DeleteMapping("/{id}")
//	public ResponseEntity<?> deleteCategory(@PathVariable Long id){
//		String username = getCurrentUsername();
//        categoryService.deleteCategory(username, id);
//        return ResponseEntity.noContent().build();
//	}
}
