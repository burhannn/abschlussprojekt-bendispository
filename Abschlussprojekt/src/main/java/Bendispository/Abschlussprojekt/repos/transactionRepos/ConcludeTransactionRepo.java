package Bendispository.Abschlussprojekt.repos.transactionRepos;

import Bendispository.Abschlussprojekt.model.transactionModels.ConcludeTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConcludeTransactionRepo extends CrudRepository<ConcludeTransaction, Long> {
    List<ConcludeTransaction> findAll();
}
