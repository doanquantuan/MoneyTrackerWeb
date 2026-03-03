package money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Transaction;
import money.entity.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

	List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    List<Transaction> findTop10ByUserOrderByTransactionDateDesc(User user);
}
