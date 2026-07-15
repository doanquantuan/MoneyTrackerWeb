package money.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import money.entity.Transaction;
import money.entity.User;
import money.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

	List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    List<Transaction> findTop10ByUserOrderByTransactionDateDesc(User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND (:accountId IS NULL OR t.account.accountId = :accountId OR t.toAccount.accountId = :accountId) " +
           "AND (:categoryId IS NULL OR t.category.categoryId = :categoryId) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:startDate IS NULL OR t.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> searchTransactions(
        @Param("user") User user,
        @Param("accountId") Long accountId,
        @Param("categoryId") Long categoryId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
