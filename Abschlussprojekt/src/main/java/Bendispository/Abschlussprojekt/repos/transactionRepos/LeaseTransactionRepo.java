package Bendispository.Abschlussprojekt.repos.transactionRepos;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaseTransactionRepo extends CrudRepository<LeaseTransaction, Long> {
	List<LeaseTransaction> findAll();

	Optional<LeaseTransaction> findByRequestId(Long id);

	List<LeaseTransaction> findAllByItemId(Long id);

	List<LeaseTransaction> findAllByLeaserAndItemIsReturnedIsFalse(Person leaser);

	List<LeaseTransaction> findAllByItemOwnerAndItemIsReturnedIsFalse(Person me);

	List<LeaseTransaction> findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(Person me);

	List<LeaseTransaction> findAllByItemIdAndEndDateGreaterThan(Long id, LocalDate now);

	List<LeaseTransaction> findAllByLeaserAndLeaseIsConcludedIsTrue(Person leaser);

	List<LeaseTransaction> findAllByItemOwnerAndLeaseIsConcludedIsTrue(Person owner);

}
