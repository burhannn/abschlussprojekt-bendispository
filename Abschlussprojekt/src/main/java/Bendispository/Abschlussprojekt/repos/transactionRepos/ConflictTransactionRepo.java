package Bendispository.Abschlussprojekt.repos.transactionRepos;

import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ConflictTransactionRepo extends CrudRepository<ConflictTransaction, Long> {
    List<ConflictTransaction> findAll();
}
