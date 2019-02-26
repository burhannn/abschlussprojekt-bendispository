package Bendispository.Abschlussprojekt.repos.transactionRepos;

import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface ConflictTransactionRepo extends CrudRepository<ConflictTransaction, Long> {
    List<ConflictTransaction> findAll();
    List<ConflictTransaction> findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse();
    Optional<ConflictTransaction> findById(Long id);
}
