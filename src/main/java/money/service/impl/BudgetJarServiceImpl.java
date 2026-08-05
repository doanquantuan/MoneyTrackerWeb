package money.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.dto.jar.BudgetJarPercentageRequest;
import money.dto.jar.BudgetJarTransferRequest;
import money.entity.BudgetJar;
import money.entity.User;
import money.repository.BudgetJarRepository;
import money.repository.UserRepository;
import money.service.IBudgetJarService;

@Service
@Transactional
public class BudgetJarServiceImpl implements IBudgetJarService {

	@Autowired
	private BudgetJarRepository budgetJarRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public List<BudgetJar> getJarsByUser(String email) {
		List<BudgetJar> jars = budgetJarRepo.findByUser_Email(email);
		if (jars.isEmpty()) {
			User user = userRepo.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
			initializeDefaultJars(user);
			return budgetJarRepo.findByUser_Email(email);
		}
		return jars;
	}

	@Override
	public void initializeDefaultJars(User user) {
		List<BudgetJar> jars = new ArrayList<>();
		
		jars.add(new BudgetJar(0, user, "Chi tiêu thiết yếu (NEC)", "NEC", 55.0, 0.0, 0.0, 0.0));
		jars.add(new BudgetJar(0, user, "Tự do tài chính (FFA)", "FFA", 10.0, 0.0, 0.0, 0.0));
		jars.add(new BudgetJar(0, user, "Tiết kiệm dài hạn (LTSS)", "LTSS", 10.0, 0.0, 0.0, 0.0));
		jars.add(new BudgetJar(0, user, "Giáo dục (EDU)", "EDU", 10.0, 0.0, 0.0, 0.0));
		jars.add(new BudgetJar(0, user, "Hưởng thụ (PLAY)", "PLAY", 10.0, 0.0, 0.0, 0.0));
		jars.add(new BudgetJar(0, user, "Cho đi (GIVE)", "GIVE", 5.0, 0.0, 0.0, 0.0));

		budgetJarRepo.saveAll(jars);
	}

	@Override
	public List<BudgetJar> updateJarPercentages(String email, List<BudgetJarPercentageRequest> requests) {
		List<BudgetJar> userJars = budgetJarRepo.findByUser_Email(email);
		if (userJars.isEmpty()) {
			throw new RuntimeException("Vui lòng khởi tạo các chiếc lọ trước");
		}

		double totalPercent = requests.stream()
				.mapToDouble(r -> r.getPercentage() != null ? r.getPercentage() : 0.0)
				.sum();

		if (Math.abs(totalPercent - 100.0) > 0.001) {
			throw new RuntimeException("Tổng tỷ lệ phần trăm của 6 chiếc lọ phải bằng đúng 100%!");
		}

		Map<Long, Double> pctMap = requests.stream()
				.collect(Collectors.toMap(BudgetJarPercentageRequest::getId, BudgetJarPercentageRequest::getPercentage));

		for (BudgetJar jar : userJars) {
			if (pctMap.containsKey(jar.getId())) {
				jar.setPercentage(pctMap.get(jar.getId()));
			}
		}

		return budgetJarRepo.saveAll(userJars);
	}

	@Override
	public void transferBetweenJars(String email, BudgetJarTransferRequest request) {
		if (request.getFromJarId().equals(request.getToJarId())) {
			throw new RuntimeException("Không thể tự chuyển tiền vào cùng một lọ!");
		}

		BudgetJar fromJar = budgetJarRepo.findById(request.getFromJarId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy lọ nguồn"));
		BudgetJar toJar = budgetJarRepo.findById(request.getToJarId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy lọ đích"));

		if (!fromJar.getUser().getEmail().equals(email) || !toJar.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Bạn không có quyền chuyển tiền giữa hai chiếc lọ này");
		}

		if (request.getAmount() <= 0) {
			throw new RuntimeException("Số tiền chuyển phải lớn hơn 0");
		}

		if (fromJar.getRemainingAmount() < request.getAmount()) {
			throw new RuntimeException("Số dư khả dụng trong chiếc lọ '" + fromJar.getName() + "' không đủ để thực hiện chuyển tiền!");
		}

		// Update both allocated and remaining to stay consistent with remaining = allocated - spent
		fromJar.setAllocatedAmount(fromJar.getAllocatedAmount() - request.getAmount());
		fromJar.setRemainingAmount(fromJar.getAllocatedAmount() - fromJar.getSpentAmount());

		toJar.setAllocatedAmount(toJar.getAllocatedAmount() + request.getAmount());
		toJar.setRemainingAmount(toJar.getAllocatedAmount() - toJar.getSpentAmount());

		budgetJarRepo.save(fromJar);
		budgetJarRepo.save(toJar);
	}
}
