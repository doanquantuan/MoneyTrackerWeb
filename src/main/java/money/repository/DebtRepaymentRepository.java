package money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.DebtRepayment;

@Repository
public interface DebtRepaymentRepository extends JpaRepository<DebtRepayment, Long> {
}
