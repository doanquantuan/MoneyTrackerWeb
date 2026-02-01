package money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.entity.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	
//	List<Account> findByUser_UserIdAndIsActiveTrue(long userId);

    List<Account> findByUser_UserId(long userId);
	
    List<Account> findByUser_Email(String email);
}
