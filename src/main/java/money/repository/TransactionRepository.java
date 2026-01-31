package money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{


}
